package ffrapid_protocol.flow_control;

import app.FFSync;
import common.Timer;
import compression.Compression;
import ffrapid_protocol.FTRapid;
import ffrapid_protocol.data.DivideData;
import ffrapid_protocol.exceptions.NoConnectionException;
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
import java.util.Objects;

import static common.debugger.Debugger.log;
import static ffrapid_protocol.FTRapid.sendAck;

public class StopAndWait {
    private static final int debuggerLevel = 2;

    public static void sendData(DatagramSocket socket, InetAddress address, int port, byte[] data)
            throws NoConnectionException {
        // Stop and wait algorithm
        // 0. Sends the amount of packets that is going to need to download the file
        // 1. Sends the file block
        // 2. Waits for the Ack
        // 2.1 [Ack is not received in the RTT] -> Sends the file block again
        // 3. Goes back into step 1 until there's no more blocks

        Timer timer = new Timer();
        data = Compression.compress(data);

        DivideData divideData = new DivideData(data);

        log("StopAndWait | Blocks: " + divideData.blocks + " lastBlockLen: " + divideData.lastBlockLen, debuggerLevel);

        // Sends the amount of packets
        StopAndWaitV2.send(new Ack(divideData.blocks), socket, address, port);
        log("StopAndWait | Sending the amount of packets");

        // Gets the blocks
        for (int i = 1; i <= divideData.blocks; i++) {
            Data dataPacket = new Data(i, divideData.getBlock(i));
            StopAndWaitV2.send(dataPacket, socket, address, port); // Sends the packet
            log("StopAndWait | DataPacket acknowledged", debuggerLevel);
        }
        log("StopAndWait | File uploaded in " + timer.getMilliseconds() + "ms", debuggerLevel);
    }



    public static void receiveFile(File file, DatagramSocket socket, InetAddress address) throws IOException {
        DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
        int port = datagramPacket.getPort();
        int packets = (int) ((Ack) Packet.deserialize(datagramPacket.getData())).segmentNumber;
        sendAck(socket, address, port, 0);
        Data data;
        ByteBuffer bb = ByteBuffer.allocate(FFSync.getMTU() * packets);


        for (int seqNumber = 1; seqNumber <= packets; seqNumber++) { // Last block included
            data = (Data) StopAndWaitV2.receive(socket); // Assuming that we will receive data
            bb.put(data.data); // Writes the data
            //log("!! Data ->" + toHexString(data.data) + " !!", 1);
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
