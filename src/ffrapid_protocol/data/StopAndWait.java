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
import static ffrapid_protocol.FTRapid.sendAck;

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
        Ack ack;

        while (!received) { // While the receiver doesn't send the ack
            try {
                FTRapid.send(packet, socket, address, port);
                Packet packetReceived = FTRapid.receive(socket);
                if (packetReceived instanceof Ack) received = true;
            } catch (IOException ignored) {
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
                if (ack.segmentNumber == data.blockNumber) received = true;
            } catch (IOException ignored) {
            }
        }
    }

    public static void sendData(DatagramSocket socket, InetAddress address, int port, byte[] data)
            throws IOException, NotAckPacket {
        // Stop and wait algorithm
        // 0. Sends the amount of packets that is going to need to download the file
        // 1. Sends the file block
        // 2. Waits for the Ack
        // 2.1 [Ack is not received in the RTT] -> Sends the file block again
        // 3. Goes back into step 1 until there's no more blocks

        Timer.startTimer();

        DivideData divideData = new DivideData(data);

        log("StopAndWait | Blocks: " + divideData.blocks + " lastBlockLen: " + divideData.lastBlockLen, debuggerLevel);

        // Sends the amount of packets
        FTRapid.send(new Ack(divideData.blocks + 1), socket, address, port);
        log("StopAndWait | Sending the amount of packets");

        // Gets the blocks
        for (int i = 0; i < divideData.blocks; i++) {
            Data dataPacket = new Data(i + 1, divideData.getBlock(i));

            send(socket, address, port, dataPacket); // Sends the packet

            log("StopAndWait | Data Packet acknowledged", debuggerLevel);
        }
        log("StopAndWait | File uploaded in " + Timer.getMiliseconds() + "ms", debuggerLevel);

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
            data = (Data) FTRapid.receive(socket); // Assuming that we will receive data

            outputStream.write(data.data); // Writes the data

            sendAck(socket, address, port, seqNumber); // Sends the Ack
        }
        outputStream.close();
    }
}
