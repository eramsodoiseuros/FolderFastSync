package common;

import ffrapid_protocol.ControlPacket;
import ffrapid_protocol.control_packet_types.Hello;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RequestHandler implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;

    public RequestHandler(DatagramSocket socket, InetAddress address, int port) {
        this.socket = socket;
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        ControlPacket controlPacket = new Hello();
        byte[] packet = controlPacket.toBytes();
        DatagramPacket data = new DatagramPacket(packet, packet.length, address, port);

        try {
            socket.send(data); // Send the hello packet
            System.out.println("Sending the hello packet");
        } catch (Exception e) {
            System.out.println("Error common.RequestHandler run [" + e.getMessage() + "]");
        }
    }
}
