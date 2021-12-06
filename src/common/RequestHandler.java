package common;

import common.debugger.Debugger;
import ffrapid_protocol.*;
import ffrapid_protocol.data_packet_types.Data;
import ffrapid_protocol.data_packet_types.Get;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;
    private final DatagramPacket initialPacket;

    public RequestHandler(DatagramSocket socket, InetAddress address, int port, DatagramPacket initialPacket) throws IOException {
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.initialPacket = initialPacket;
    }

    @Override
    public void run() {
        Packet packet = Packet.deserialize(initialPacket.getData());
        String str;
        if (packet instanceof Data) Debugger.print((str = new String(((Data) packet).getData(), StandardCharsets.UTF_8)));
        else str = "";
        Data data = new Data(100, str.getBytes(StandardCharsets.UTF_8));
        byte[] packetToSend = data.serialize();

        DatagramPacket datagramPacket = new DatagramPacket(packetToSend, packetToSend.length, address, port);

        try {
            socket.send(datagramPacket);
            System.out.println("Sending the hello packet");
        } catch (Exception e) {
            System.out.println("Error common.RequestHandler run [" + e.getMessage() + "]");
        }
    }

    private void sendMetaData(DatagramSocket socket) {


    }
}
