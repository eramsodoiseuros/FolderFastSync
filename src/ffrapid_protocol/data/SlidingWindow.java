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
import java.nio.file.Files;
import java.nio.file.Paths;

import static common.debugger.Debugger.log;
import static ffrapid_protocol.FTRapid.*;
import static ffrapid_protocol.data.DivideData.getBlock;
import static ffrapid_protocol.data.DivideData.getLastBlock;

/**
 * Sliding window protocol with Go Back N in the receiver side.
 */
public class SlidingWindow {
    private static final int debuggerLevel = 2;
    private static final int windowSize = 10;

    public static void sendData(DatagramSocket socket, InetAddress address, int port, byte[] data)
            throws IOException, NotAckPacket {
        // Ler ack
        // Mandar block

        // Accumulative algorithm
        // 1. Divide the File in blocks
        // 2. Send each n blocks and wait for an Ack
        // 3. See if until block number did it get to
        // 4. Start sending with the block number missing
        // 5. Optional receive Ack in the middle of the sending process
        // 6. Read this Ack to see what has to be sent


        Timer timer = new Timer();

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
            boolean receiveAck = false;
            // Sends the packet
            for (int ii = 0; ii < windowSize && !receiveAck; ii++) {
                FTRapid.send(dataPacket, socket, address, port);
                ack = (Ack) receive(socket);
                if (ack.segmentNumber == i + ii) {
                    System.out.println("Recebeu direito");
                    receiveAck = true;
                } else System.out.println("Volta a enviar");
                log("StopAndWait | Data Packet acknowledged", debuggerLevel);
            }

            // Waits for the Ack
            //if (receivesAck(socket, address, port).segmentNumber - 1 != i) i--;
        }
        // Gets the last block
        Data dataPacket = getLastBlock(lastBlockLen, data, blocks, MTU);

        // Sends the packet
        FTRapid.send(dataPacket, socket, address, port);

        // Waits for the Ack
        receivesAck(socket, address, port);

        log("StopAndWait | File uploaded in " + timer.getMilliseconds() + "ms", debuggerLevel);

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

    /**
     * Sends a file.
     *
     * @param fileName the name of the file.
     */
    public static void sendFile(String fileName, DatagramSocket socket, InetAddress address, int port) {
        try {
            StopAndWait.sendData(socket, address, port, Files.readAllBytes(Paths.get(FFSync.getCurrentDirectory() + "/" + fileName)));
        } catch (NotAckPacket | IOException e) {
            e.printStackTrace();
        }
    }

    public static void receiveFile(FileOutputStream outputStream, DatagramSocket socket, InetAddress address)
            throws IOException {
        // Stop and wait algorithm
        DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
        int port = datagramPacket.getPort();
        long packets = ((Ack) Packet.deserialize(datagramPacket.getData())).segmentNumber;
        Data data;

        for (int seqNumber = 0; seqNumber < packets; seqNumber++) { // Last block included

            for (int j = 0; j < windowSize; j++) {
                data = (Data) FTRapid.receive(socket); // Assuming that we will receive data
                long seqN = data.blockNumber;
                if (seqN == seqNumber + 1) {
                    System.out.println("Pacote correto");
                    seqNumber++;
                } else {
                    sendAck(socket, address, port, seqNumber); // Sends the Ack
                }
                if (j == windowSize) ;
                outputStream.write(data.data); // Writes the data
            }

            sendAck(socket, address, port, seqNumber); // Sends the Ack
        }
        outputStream.close();
    }
}
