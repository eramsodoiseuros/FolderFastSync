package test;

import ffrapid_protocol.FTRapid;
import ffrapid_protocol.Packet.Data;

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
                int i = 0;
                int port = 12345;

                // Connecting
                DatagramPacket datagramPacket;
                byte[] bytes;
                //print("Connecting...");

                while (running) {
                    Data dataSent = new Data(0, "Hello".getBytes(StandardCharsets.UTF_8));
                    FTRapid.send(dataSent, socket, InetAddress.getByName("localhost"), port);
                    log("Packet sent");

                    Data dataReceived = (Data) FTRapid.receive(socket);
                    log("Packet received");
                    log("Data: " + new String(dataReceived.getData(), StandardCharsets.UTF_8));
                    Thread.sleep(5000);
                    if (i++ >= 5) running = false;
                }
                socket.close();
            } catch (Exception e) {
                log("Exception: " + e.getLocalizedMessage());
            }

        }).start();


    }
}
