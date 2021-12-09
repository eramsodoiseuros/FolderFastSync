package ffrapid_protocol;

import app.FFSync;
import common.Node;
import ffrapid_protocol.packet.Get;
import ffrapid_protocol.packet.Metadata;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static common.debugger.Debugger.log;

/**
 * Sends FFRapidProtocol.FTRapid data.
 */
public class Sender implements Runnable {

    @Override
    public void run() {
        // Sends a request in the beginning of the program and with changes in the directory
        // https://docs.oracle.com/javase/tutorial/essential/io/notification.html
        Node n = FFSync.getNodes().get(0); // Vamos começar por uma ligação apenas
        int MTU = FFSync.getMTU();

        try {
            DatagramSocket socket = new DatagramSocket(); // creates a socket - port not define - system gives an available port

            log("Sender | First node ip: " + n.getAddress().toString());

            Metadata metadata = requestsAllMetadata(socket, n.getAddress(), FFSync.getPORT());

            log("Sender | Finishing");

        } catch (Exception e) {
            System.out.println("Error sender - run [" + e.getMessage() + "]");
        }
    }

    public Metadata requestsAllMetadata(DatagramSocket socket, InetAddress address, int port) throws IOException {
        Get get = new Get(true, true); // Metadata from all files

        FTRapid.send(get, socket, address, port); // Sends the request

        log("Sender | Packet sent");

        Metadata metadata = (Metadata) FTRapid.receive(socket); // Receives the response

        log("Sender | Packet received");

        return metadata;
    }
}
