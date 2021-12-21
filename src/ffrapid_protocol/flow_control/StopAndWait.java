package ffrapid_protocol.flow_control;

import app.FFSync;
import common.Timer;
import compression.Compression;
import ffrapid_protocol.FTRapid;
import ffrapid_protocol.data.DivideData;
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
import static common.debugger.Debugger.toHexString;

public class StopAndWait {
    private static final int debuggerLevel = 2;

    /**
     * Sends a packet, guarantying that arrives. Waits for the acknowledgment from the other side.
     * If the acknowledgment isn't received in the established timeout time, the packet is resented.
     *
     * @param socket  a socket
     * @param address an address
     * @param port    a port
     * @param packet  a
     */
    public static void send(DatagramSocket socket, InetAddress address, int port, Packet packet) {
        boolean received = false;

        while (!received) { // While the receiver doesn't send the ack
            try {
                FTRapid.send(packet, socket, address, port);
                Packet packetReceived = FTRapid.receive(socket);
                if (packetReceived instanceof Ack) received = true;
            } catch (IOException ignored) {
                log("StopAndWait | Ack not received in the given time, sending the packet again...", debuggerLevel);
            }
        }
    }

    /**
     * Similar to the above method, but it sends a Data packet and checks if the acknowledgment has the right sequence number.
     *
     * @param socket  a socket
     * @param address an address
     * @param port    a port
     * @param data    a data packet
     */
    private static void send(DatagramSocket socket, InetAddress address, int port, Data data) {
        boolean received = false;
        Ack ack;

        while (!received) { // While the receiver doesn't send the ack
            try {
                FTRapid.send(data, socket, address, port);
                ack = (Ack) FTRapid.receive(socket);
                if (true || ack.segmentNumber == data.blockNumber) received = true;
            } catch (IOException ignored) {
            }
        }
    }

    public static Packet receive(DatagramSocket socket) throws IOException {
        DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
        Packet packet = Packet.deserialize(datagramPacket.getData());
        int seqNumber = 10;
        if (packet instanceof Data) seqNumber = ((Data) packet).blockNumber;
        FTRapid.send(new Ack(10), socket, datagramPacket.getAddress(), datagramPacket.getPort());
        return packet;
    }

    public static void sendData(DatagramSocket socket, InetAddress address, int port, byte[] data) throws IOException, NotAckPacket {
        // Stop and wait algorithm
        // 0. Sends the amount of packets that is going to need to download the file
        // 1. Sends the file block
        // 2. Waits for the Ack
        // 2.1 [Ack is not received in the RTT] -> Sends the file block again
        // 3. Goes back into step 1 until there's no more blocks


        DivideData divideData = new DivideData(Compression.compress(data));

        log("StopAndWait | Blocks: " + divideData.blocks + " lastBlockLen: " + divideData.lastBlockLen, debuggerLevel);

        Timer timer = new Timer();

        // Sends the amount of packets
        StopAndWait.send(socket, address, port, new Ack(divideData.blocks));
        log("StopAndWait | Sending the amount of packets");

        // Gets the blocks
        for (int i = 0; i < divideData.blocks - 1; i++) {
            Data dataPacket = new Data(i + 1, divideData.getBlock(i));

            send(socket, address, port, dataPacket); // Sends the packet

            log("StopAndWait | Data Packet acknowledged", debuggerLevel);
        }
        log("StopAndWait | Data uploaded in " + timer.getMilliseconds() + "ms", debuggerLevel);
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
        int packets = ((Ack) Packet.deserialize(datagramPacket.getData())).segmentNumber;
        FTRapid.send(new Ack(1), socket, address, port);
        ByteBuffer bb = ByteBuffer.allocate(FFSync.getMTU() * packets);
        Data data;

        for (int seqNumber = 1; seqNumber < packets; seqNumber++) { // Last block included
            data = (Data) StopAndWait.receive(socket); // Assuming that we will receive data
            bb.put(data.data); // Writes the data
            log("!! Data ->" + toHexString(data.data) + " !!", 1);
        }

        byte[] fileCompressed = bb.array();
        log("!! " + toHexString(fileCompressed) + " !!");
        byte[] fileDecompressed = Compression.decompress(fileCompressed);

        assert fileDecompressed != null;
        log("StopAndWait | Received file with compression of " + (double) fileCompressed.length / fileDecompressed.length, debuggerLevel);

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(Objects.requireNonNull(fileDecompressed));
        outputStream.close();
    }
}
