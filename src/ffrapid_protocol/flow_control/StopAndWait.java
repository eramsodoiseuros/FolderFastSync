package ffrapid_protocol.flow_control;

import app.FFSync;
import common.Timer;
import ffrapid_protocol.FTRapid;
import ffrapid_protocol.control_packets.Ack;
import ffrapid_protocol.control_packets.ControlPacket;
import ffrapid_protocol.control_packets.Data;
import ffrapid_protocol.control_packets.Size;
import ffrapid_protocol.data.PacketToSegment;
import ffrapid_protocol.packet.Packet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import static common.debugger.Debugger.log;
import static ffrapid_protocol.FTRapid.sendAck;

public class StopAndWait implements FlowControl{
    private static final int debuggerLevel = 2;
    private final int MTU = FFSync.getMTU() - Data.headerLength;
    private int segmentNumber = 0;

    public static void receiveFile(FileOutputStream outputStream, DatagramSocket socket, InetAddress address)
            throws IOException {
        // Stop and wait algorithm
        DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
        int port = datagramPacket.getPort();
        long packets = ((Ack) ControlPacket.deserialize(datagramPacket.getData())).segmentNumber;
        Data data;

        for (int seqNumber = 0; seqNumber < packets; seqNumber++) { // Last block included
            data = (Data) FTRapid.receive(socket); // Assuming that we will receive data

            outputStream.write(data.data); // Writes the data

            sendAck(socket, address, port, seqNumber); // Sends the Ack
        }
        outputStream.close();
    }

    public void send(Packet packet, DatagramSocket socket, InetAddress address, int port) throws IOException {
        Timer timer = new Timer();
        PacketToSegment packetToSegment = new PacketToSegment(packet);
        log("StopAndWait | Blocks: " + packetToSegment.blocks + " lastBlockLen: " + packetToSegment.lastBlockLen, debuggerLevel);

        Size size = new Size(packetToSegment.type, segmentNumber, packetToSegment.blocks);
        FTRapid.send(size, socket, address, port);

        for (int i = 0; i < packetToSegment.blocks; i++) {
            byte[] data = packetToSegment.getBlock(i); // Gets the data
            boolean received = false;
            Ack ack;

            send(socket, address, port, data);
            segmentNumber++;
        }
        log("StopAndWait | ControlPacket sent in " + timer.getMilliseconds() + "ms", debuggerLevel);
    }

    private void send(DatagramSocket socket, InetAddress address, int port, byte[] data) {
        boolean received = false;
        Ack ack;

        while (!received) { // While the receiver doesn't send the ack
            try {
                FTRapid.send(new Data(segmentNumber, data), socket, address, port);
                ack = (Ack) FTRapid.receive(socket);
                if (ack.segmentNumber == segmentNumber) received = true;
            } catch (IOException ignored) {
            }
        }
    }

    public Packet receive(DatagramSocket socket) throws IOException {
        ByteBuffer bb;
        Ack ack;

        DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
        InetAddress address = datagramPacket.getAddress();
        int port = datagramPacket.getPort();

        Size size = (Size) ControlPacket.deserialize(datagramPacket.getData());

        bb = ByteBuffer.allocate(size.size * MTU); // Allocating the space to store the packet
        ack = new Ack(size.sequenceNumber);
        FTRapid.send(ack, socket, address, port);

        for (int i = 0; i < size.size; i++) {
            Data data = (Data) FTRapid.receive(socket);
            ack = new Ack(data.blockNumber);
            FTRapid.send(ack, socket, address, port);
            bb.put(data.data);
        }
        return Packet.deserialize(bb, size.type);
    }
}
