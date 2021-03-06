package ffrapid_protocol;

import app.FFSync;
import common.debugger.Debugger;
import ffrapid_protocol.operations.RequestHandler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Receives FFRapidProtocol.FTRapid data.
 */
public class Receiver implements Runnable {

    private DatagramSocket serverSocket;
    @SuppressWarnings({"FieldMayBeFinal", "CanBeFinal", "FieldCanBeLocal"})
    private boolean running = true;

    public Receiver() {
        try {
            this.serverSocket = new DatagramSocket(FFSync.getPORT());
        } catch (SocketException e) {
            System.out.println("Error FTRapid_protocol.Receiver - FTRapid_protocol.Receiver [" + e.getMessage() + "]");
        }
    }

    @Override
    public void run() {
        try {
            while (running) { // infinite loop - very bad practice
                DatagramPacket received = FTRapid.receiveDatagram(serverSocket);

                Debugger.log("Receiver | New connection with address: " + received.getAddress() + " and port " + received.getPort());
                DatagramSocket socket = new DatagramSocket();
                Thread t = new Thread(new RequestHandler(socket, received.getAddress(), received.getPort(), received));
                t.start();
            }
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("Error receiver - run [" + e.getMessage() + "]");
        }
    }
}
