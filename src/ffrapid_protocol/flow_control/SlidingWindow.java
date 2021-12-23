package ffrapid_protocol.flow_control;

import app.FFSync;
import common.Timer;
import compression.Compression;
import ffrapid_protocol.FTRapid;
import ffrapid_protocol.data.DivideData;
import ffrapid_protocol.exceptions.NoConnectionException;
import ffrapid_protocol.exceptions.NotAckPacket;
import ffrapid_protocol.packet.Ack;
import ffrapid_protocol.packet.Data;
import ffrapid_protocol.packet.Packet;
import hmac.PacketCorruptedException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Objects;

import static common.debugger.Debugger.log;
import static common.debugger.Debugger.toHexString;
import static ffrapid_protocol.FTRapid.sendAck;

public class SlidingWindow {
    private static final int debuggerLevel = 2;
    private static final int windowSize = 10;

    public static void sendData(DatagramSocket socket, InetAddress address, int port, byte[] data) throws NoConnectionException, IOException, NotAckPacket {
        // Accumulative algorithm
        // 1. Divide the File in blocks
        // 2. Send each n blocks and wait for an Ack
        // 3. See if until block number did it get to
        // 4. Start sending with the block number missing
        // 5. Optional receive Ack in the middle of the sending process
        // 6. Read this Ack to see what has to be sent

        Timer timer = new Timer();
        data = Compression.compress(data);
        Ack ack;

        DivideData divideData = new DivideData(data);

        log("SlidingWindow | Blocks: " + divideData.blocks + " lastBlockLen: " + divideData.lastBlockLen, debuggerLevel);

        // Sends the amount of packets
        StopAndWait.send(new Ack(divideData.blocks), socket, address, port);
        log("SlidingWindow | Sending the amount of packets");

        // Gets the blocks
        for (int i = 1; i <= divideData.blocks; ) {
            try {
                for (int j = 0; j < windowSize && i + j <= divideData.blocks; j++) {
                    Data dataPacket = new Data(i + j, divideData.getBlock(i + j));
                    FTRapid.send(dataPacket, socket, address, port); // Sends the packet
                }
                ack = FTRapid.receivesAck(socket);
                i = (int) (ack.segmentNumber + 1);
            } catch (SocketTimeoutException e) {
                log("SlidingWindow | Sending Data - SocketTimeout...");
            } catch (PacketCorruptedException e) {
                log("Packet corrupted!", debuggerLevel);
            }

            log("SlidingWindow | DataPacket's acknowledged", debuggerLevel);
        }
        long time = timer.getMilliseconds();
        log("SlidingWindow | File uploaded in " + time + "ms", debuggerLevel);
        log("SlidingWindow | Download speed: " + (data.length / (time * 1e3)) + "mb/s");
    }


    public static void receiveFile(File file, DatagramSocket socket, InetAddress address) throws IOException {
        DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
        int port = datagramPacket.getPort();
        int packets = 0;
        try {
            packets = (int) ((Ack) Packet.deserialize(datagramPacket.getData())).segmentNumber;
        } catch (PacketCorruptedException ignored) {
        }
        sendAck(socket, address, port, 0);
        Data data;
        ByteBuffer bb = ByteBuffer.allocate(FFSync.getMTU() * packets);


        for (int i = 1; i <= packets; ) {
            int minJ = i - 1;
            try {
                for (int j = 0; j < windowSize && i + j <= packets; j++) {
                    data = (Data) FTRapid.receive(socket); // Sends the packet
                    if (data.blockNumber == minJ + 1) {
                        bb.put(data.data);
                        minJ++;
                    }
                }
                FTRapid.sendAck(socket, address, port, minJ);
            } catch (SocketTimeoutException e) {
                log("SlidingWindow | Receiving file - SocketTimeout...");
            } catch (PacketCorruptedException e) {
                log("Packet corrupted!", debuggerLevel);
            }
            i = minJ + 1;
            log("SlidingWindow | DataPacket's acknowledged", debuggerLevel);
        }


        byte[] fileCompressed = bb.array();
        log("SlidingWindow | Compressed File: " + toHexString(fileCompressed), debuggerLevel + 2);
        byte[] fileDecompressed = Compression.decompress(fileCompressed);

        assert fileDecompressed != null;
        log("SlidingWindow | Received file with compression of " + (double) (fileCompressed.length - fileDecompressed.length) / fileDecompressed.length, debuggerLevel);

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(Objects.requireNonNull(fileDecompressed));
        outputStream.close();
    }
}
