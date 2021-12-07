package ffrapid_protocol;

import app.FFSync;
import common.RequestHandler;
import common.debugger.Debugger;
import ffrapid_protocol.FTRapid;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Receives FFRapidProtocol.FTRapid data.
 */
public class Receiver implements Runnable {
    private final int MTU = FFSync.getMTU();

    private DatagramSocket serverSocket;
    private boolean running = true;

    public Receiver() {
        try {
            this.serverSocket = new DatagramSocket(FFSync.getPORT()); // mudar isto dps
        } catch (SocketException e) {
            System.out.println("error ffrapid_protocol.Receiver - ffrapid_protocol.Receiver [" + e.getMessage() + "]");
        }
    }

    @Override
    public void run() {
        try {
            while (running) {                                           // infinite loop - very bad pratice
                DatagramPacket received = FTRapid.receiveDatagram(serverSocket);

                Debugger.log("New connection with address: " + received.getAddress() + " and port " + received.getPort());
                DatagramSocket socket = new DatagramSocket();
                Thread t = new Thread(new RequestHandler(socket, received.getAddress(), received.getPort(), received));
                t.start();
            }
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("erro receiver - run [" + e.getMessage() + "]");
        }
    }
}
