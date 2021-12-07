package test;

import ffrapid_protocol.Packet;
import ffrapid_protocol.data_packet_types.Data;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static common.debugger.Debugger.*;


public class SendingData {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                boolean running = true;
                int port = 12345;

                // Connecting
                DatagramPacket datagramPacket;
                byte[] bytes;
                //print("Connecting...");

                while (running) {
                    Data data = new Data(0, "Hello".getBytes(StandardCharsets.UTF_8));
                    bytes = data.serialize();
                    datagramPacket =
                            new DatagramPacket(bytes, bytes.length, InetAddress.getByName("localhost"), port);

                    socket.send(datagramPacket);
                    log("Packet sent");

                    socket.receive(datagramPacket);
                    //port = datagramPacket.getPort();
                    log("Port: " + port);

                    Data packet = (Data) Packet.deserialize(datagramPacket.getData());
                    log("Packet received");
                    log("Data: " + new String(packet.getData(), StandardCharsets.UTF_8));
                    Thread.sleep(5000);
                }
                socket.close();
            } catch (Exception e) {
                log("Exception: " + e.getLocalizedMessage());
            }

        }).start();


    }
}
