package ffrapid_protocol.operations;

import common.Timer;
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
    private int timeout;
    private final int coef = 4;

    public RequestHandler(DatagramSocket socket, InetAddress address, int port, DatagramPacket initialPacket) {
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.initialPacket = initialPacket;
    }

    @Override
    public void run() {
        try {
            Timer timer = new Timer();
            FTRapid.sendAck(socket, address, port, 0); // Send the ack to acknowledge that the request as been received.
            timeout = (int) (timer.getMilliseconds() * coef); // Calculating the RTT
            socket.setSoTimeout(timeout);// Setting the timeout in the socket
            log("RequestHandler | Timeout: " + timeout);

            Packet packet = Packet.deserialize(initialPacket.getData());

            packet.handle(socket, address, port);

            log("RequestHandler | Packet handled.");
        } catch (Exception e) {
            System.out.println("Error RequestHandler run [" + e.getMessage() + "]");
            e.printStackTrace();
        }
    }



}
