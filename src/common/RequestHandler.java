package common;

import common.debugger.Debugger;
import ffrapid_protocol.Packet;
import ffrapid_protocol.data_packet_types.Data;

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

    public RequestHandler(DatagramSocket socket, InetAddress address, int port, DatagramPacket initialPacket) {
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.initialPacket = initialPacket;
    }

    @Override
    public void run() {
        try {
            Packet packet = Packet.deserialize(initialPacket.getData());
            String str;

            // Testing
            if (packet instanceof Data dataPacket) {
                Debugger.print((str = new String(dataPacket.getData(), StandardCharsets.UTF_8)));
            } else str = "";
            Data data = new Data(100, str.getBytes(StandardCharsets.UTF_8));

            byte[] packetToSend = data.serialize();

            DatagramPacket datagramPacket = new DatagramPacket(packetToSend, packetToSend.length, address, port);

            socket.send(datagramPacket);
            Debugger.print("[Packet sent]");
            Debugger.print("[Port: " + datagramPacket.getPort() + "]");
        } catch (Exception e) {
            System.out.println("Error common.RequestHandler run [" + e.getMessage() + "]");
        }
    }

    private void sendMetaData(DatagramSocket socket) {


    }
}
