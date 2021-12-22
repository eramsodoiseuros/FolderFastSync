package ffrapid_protocol.flow_control;

import ffrapid_protocol.FTRapid;
import ffrapid_protocol.exceptions.NoConnectionException;
import ffrapid_protocol.packet.Ack;
import ffrapid_protocol.packet.Data;
import ffrapid_protocol.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static common.debugger.Debugger.log;

public class StopAndWaitV2 {
    private static final int debuggerLevel = 2;
    private static final int tries = 3;
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;

    public StopAndWaitV2(DatagramSocket socket, InetAddress address, int port) {
        this.socket = socket;
        this.address = address;
        this.port = port;
    }


    /**
     * @param packet a packet
     * @param socket a socket
     * @param address a address
     * @param port a port
     */
    public static void send(Packet packet, DatagramSocket socket, InetAddress address, int port) throws NoConnectionException {
        boolean received = false;
        int i = 0;
        // int portReceived = 0;

        while (i < tries && !received) { // While the receiver doesn't send the ack
            try {
                FTRapid.send(packet, socket, address, port);
                DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
                // portReceived = datagramPacket.getPort();
                Packet packetReceived = Packet.deserialize(datagramPacket.getData());
                if (packetReceived instanceof Ack) received = true;
            } catch (IOException ignored) {
                i++;
                log("StopAndWait | Ack not received in the given time, sending the packet again...", debuggerLevel);
            }
        }
        if (i == tries) throw new NoConnectionException();
        //return portReceived;
    }

    public static Packet receive(DatagramSocket socket) throws IOException {
        DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
        Packet packet = Packet.deserialize(datagramPacket.getData());
        int seqNumber = 0;
        if (packet instanceof Data) seqNumber = (int) ((Data) packet).blockNumber;
        FTRapid.send(new Ack(seqNumber), socket, datagramPacket.getAddress(), datagramPacket.getPort());
        return packet;
    }

    /**
     * Sends a packet, guarantying that arrives. Waits for the acknowledgment from the other side.
     * If the acknowledgment isn't received in the established timeout time, the packet is resented.
     *
     * @param packet a packet
     */
    public void send(Packet packet) {
        boolean received = false;
        int i = 0;

        while (i < tries && !received) { // While the receiver doesn't send the ack
            try {
                FTRapid.send(packet, socket, address, port);
                Packet packetReceived = FTRapid.receive(socket);
                if (packetReceived instanceof Ack) received = true;
            } catch (IOException ignored) {
                i++;
                log("StopAndWait | Ack not received in the given time, sending the packet again...", debuggerLevel);
            }
        }
    }

    /**
     * Similar to the above method, but it sends a Data packet and checks if the acknowledgment has the right sequence number.
     *
     * @param data a data packet
     */
    private void send(Data data) {
        boolean received = false;
        Ack ack;
        int i = 0;

        while (i < tries && !received) { // While the receiver doesn't send the ack
            try {
                FTRapid.send(data, socket, address, port);
                ack = (Ack) FTRapid.receive(socket);
                if (ack.segmentNumber == data.blockNumber) received = true;
            } catch (IOException ignored) {
                i++;
                log("StopAndWait | Ack not received in the given time, sending the packet again...", debuggerLevel);
            }
        }
    }

    public Packet receive() throws IOException {
        DatagramPacket datagramPacket = FTRapid.receiveDatagram(socket);
        Packet packet = Packet.deserialize(datagramPacket.getData());
        int seqNumber = 0;
        if (packet instanceof Data) seqNumber = (int) ((Data) packet).blockNumber;
        FTRapid.send(new Ack(seqNumber), socket, datagramPacket.getAddress(), datagramPacket.getPort());
        return packet;
    }
}
