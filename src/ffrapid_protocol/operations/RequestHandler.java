package ffrapid_protocol.operations;

import ffrapid_protocol.FTRapid;
import ffrapid_protocol.packet.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static common.debugger.Debugger.log;

/**
 * Handles the requests received.
 */
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
            FTRapid.sendAck(socket, address, port, 0); // Send the ack to acknowledge that the request as been received.

            Packet packet = Packet.deserialize(initialPacket.getData());

            packet.handle(socket, address, port);

            log("RequestHandler | Packet handled.");
        } catch (Exception e) {
            System.out.println("Error RequestHandler run [" + e.getMessage() + "]");
            e.printStackTrace();
        }
    }



}
