package ffrapid_protocol.operations;

import ffrapid_protocol.control_packets.ControlPacket;
import ffrapid_protocol.packet.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static common.debugger.Debugger.log;

/**
 * Handles the requests received.
 */
public record RequestHandler(DatagramSocket socket, InetAddress address, int port,
                             DatagramPacket initialPacket) implements Runnable {

    @Override
    public void run() {
        try {
            Packet packet = ControlPacket.deserialize(initialPacket.getData());

            packet.handle(socket, address, port);

            log("RequestHandler | Packet handled.");
        } catch (Exception e) {
            System.out.println("Error RequestHandler run [" + e.getMessage() + "]");
            e.printStackTrace();
        }
    }


}
