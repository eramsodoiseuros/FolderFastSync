package common;

import ffrapid_protocol.*;
import ffrapid_protocol.control_packet_types.Hello;
import ffrapid_protocol.data_packet_types.Get;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RequestHandler implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;
    private final DatagramPacket initialPacket;

    public RequestHandler(DatagramSocket socket, InetAddress address, int port, DatagramPacket initialPacket) {
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.initialPacket = initialPacket;
    }

    @Override
    public void run() {
        Packet packet = Packet.deserialize(initialPacket.getData());
        PacketType type = packet.getType();
        DataPacketType dataType;
        ControlPacketType controlType;
        switch (type) {
            case Control:
                controlType = ((ControlPacket) packet).getControlType();
                switch (controlType) {
                    case Hello:
                        sendMetaData(socket);
                        break;

                    case Ack:
                        break;


                }
                break;

            case Data -> break;

            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

        DatagramPacket data = new DatagramPacket(packet, packet.length, address, port);

        try {
            socket.send(data); // Send the hello packet
            System.out.println("Sending the hello packet");
        } catch (Exception e) {
            System.out.println("Error common.RequestHandler run [" + e.getMessage() + "]");
        }
    }

    private void sendMetaData(DatagramSocket socket) {


    }
}
