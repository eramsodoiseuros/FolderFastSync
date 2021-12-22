package ffrapid_protocol.data;

import app.FFSync;
import common.Timer;
import compression.Compression;
import ffrapid_protocol.FTRapid;
import ffrapid_protocol.exceptions.NotAckPacket;
import ffrapid_protocol.packet.Ack;
import ffrapid_protocol.packet.Data;
import ffrapid_protocol.packet.Packet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static common.debugger.Debugger.log;
import static ffrapid_protocol.FTRapid.*;
import static ffrapid_protocol.data.DivideData.getBlock;
import static ffrapid_protocol.data.DivideData.getLastBlock;

public class StopAndWait {
    private static final int debuggerLevel = 2;

    public static void sendData(DatagramSocket socket, InetAddress address, int port, byte[] data)
            throws IOException, NotAckPacket {
        // Stop and wait algorithm
        // 0. Sends the amount of packets that is going to need to download the file
        // 1. Sends the file block
        // 2. Waits for the Ack
        // 2.1 [Ack is not received in the RTT] -> Sends the file block again
        // 3. Goes back into step 1 until there's no more blocks

        Timer timer = new Timer();
        data = Compression.compress(data);

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

    public static void receiveFile(File file, DatagramSocket socket, InetAddress address) throws IOException {
        DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
        int port = datagramPacket.getPort();
        int packets = (int) ((Ack) Packet.deserialize(datagramPacket.getData())).segmentNumber;
        Data data;
        ByteBuffer bb = ByteBuffer.allocate(FFSync.getMTU() * packets);


        for (int seqNumber = 1; seqNumber <= packets; seqNumber++) { // Last block included
            data = (Data) FTRapid.receive(socket); // Assuming that we will receive data
            bb.put(data.data); // Writes the data
            //log("!! Data ->" + toHexString(data.data) + " !!", 1);
            sendAck(socket, address, port, seqNumber); // Sends the Ack
        }

        byte[] fileCompressed = bb.array();
        //log("!! " + toHexString(fileCompressed) + " !!");
        byte[] fileDecompressed = Compression.decompress(fileCompressed);

        assert fileDecompressed != null;
        log("StopAndWait | Received file with compression of " + (double) fileCompressed.length / fileDecompressed.length, debuggerLevel);

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(Objects.requireNonNull(fileDecompressed));
        outputStream.close();
    }

}
