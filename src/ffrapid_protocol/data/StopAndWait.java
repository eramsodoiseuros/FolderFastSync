package ffrapid_protocol.data;

import app.FFSync;
import common.Timer;
import ffrapid_protocol.FTRapid;
import ffrapid_protocol.exceptions.NotAckPacket;
import ffrapid_protocol.packet.Ack;
import ffrapid_protocol.packet.Data;
import ffrapid_protocol.packet.Packet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static common.debugger.Debugger.log;
import static ffrapid_protocol.FTRapid.*;
import static ffrapid_protocol.data.DivideData.getBlock;
import static ffrapid_protocol.data.DivideData.getLastBlock;

public class StopAndWait {
    private static final int debuggerLevel = 0;

    public static void sendData(DatagramSocket socket, InetAddress address, int port, byte[] data)
            throws IOException, NotAckPacket {
        // Stop and wait algorithm
        // 0. Sends the amount of packets that is gonna need to download the file
        // 1. Sends the file block
        // 2. Waits for the Ack
        // 2.1 [Ack is not received in the RTT] -> Sends the file block again
        // 3. Goes back into step 1 until there's no more blocks

        Timer.startTimer();

        int MTU = FFSync.getMTU() - Data.headerLength;
        int blocks = data.length / MTU;
        int lastBlockLen = data.length % MTU;
        Ack ack;

        log("StopAndWait | Blocks: " + blocks + " lastBlockLen: " + lastBlockLen, debuggerLevel);

        // Sends the amount of packets
        FTRapid.send(new Ack(blocks + 1), socket, address, port);
        log("StopAndWait | Sending the amount of packets");

        // Gets the blocks
        for (int i = 0; i < blocks; i++) {
            Data dataPacket = getBlock(MTU, data, i);

            // Sends the packet
            FTRapid.send(dataPacket, socket, address, port);

            // Waits for the Ack
            //if (receivesAck(socket, address, port).segmentNumber - 1 != i) i--;
            receive(socket);
            log("StopAndWait | Data Packet acknowledged", debuggerLevel);
        }
        // Gets the last block
        Data dataPacket = getLastBlock(lastBlockLen, data, blocks, MTU);

        // Sends the packet
        FTRapid.send(dataPacket, socket, address, port);

        // Waits for the Ack
        receivesAck(socket, address, port);

        log("StopAndWait | File uploaded in " + Timer.getMiliseconds() + "ms", debuggerLevel);

        // Ler ack
        // Mandar block

        // Accumulative algorithm
        // 1. Divide the File in blocks
        // 2. Send each n blocks and wait for an Ack
        // 3. See if until block number did it get to
        // 4. Start sending with the block number missing
        // 5. Optional receive Ack in the middle of the sending process
        // 6. Read this Ack to see what has to be sent

    }

    public static void receiveFile(FileOutputStream outputStream, DatagramSocket socket, InetAddress address)
            throws IOException {
        // Stop and wait algorithm
        DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
        int port = datagramPacket.getPort();
        long packets = ((Ack) Packet.deserialize(datagramPacket.getData())).segmentNumber;
        Data data;

        for (int seqNumber = 0; seqNumber < packets; seqNumber++) { // Last block included
            data = (Data) FTRapid.receive(socket); // Assuming that we will receive data

            outputStream.write(data.data); // Writes the data

            sendAck(socket, address, port, seqNumber); // Sends the Ack
        }
        outputStream.close();
    }
}
