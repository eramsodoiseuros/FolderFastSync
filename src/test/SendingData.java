package test;

import common.debugger.Debugger;
import ffrapid_protocol.Packet;
import ffrapid_protocol.data_packet_types.Data;
import ffrapid_protocol.data_packet_types.Hello;

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
                Hello hello = new Hello(12346);
                byte[] bytes = hello.serialize();
                print("[Message: " + hello.getPort() + " ]");
                DatagramPacket datagramPacket =
                        new DatagramPacket(bytes, bytes.length, InetAddress.getByName("localhost"), port);

                socket.send(datagramPacket);
                print("Connecting...");

                socket.receive(datagramPacket);
                hello = (Hello) Packet.deserialize(datagramPacket.getData());
                port = hello.getPort();

                while (running) {
                    Data data = new Data(0, "Hello".getBytes(StandardCharsets.UTF_8));
                    bytes = data.serialize();
                    datagramPacket =
                            new DatagramPacket(bytes, bytes.length, InetAddress.getByName("localhost"), port);

                    socket.send(datagramPacket);
                    print("[Packet sent]");

                    socket.receive(datagramPacket);
                    port = datagramPacket.getPort();
                    print(String.valueOf(port));

                    Data packet = (Data) Packet.deserialize(datagramPacket.getData());
                    print("[Packet received]");
                    print(new String(packet.getData(), StandardCharsets.UTF_8));
                    Thread.sleep(5000);
                }
                socket.close();
            } catch (Exception e) {
                print("Exception: " + e.getLocalizedMessage());
            }

        }).start();


    }
}
