import FFRapidProtocol.FTRapid;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Receives FFRapidProtocol.FTRapid data.
 */
public class Receiver implements Runnable {
    private final int MTU = FFSync.getMTU();

    private final FFSync ffSync;
    private DatagramSocket socket;
    private boolean running = true;

    public Receiver(FFSync ffSync) {
        this.ffSync = ffSync;
        try {
            this.socket = new DatagramSocket(FFSync.getPORT()); // mudar isto dps
        } catch (SocketException e) {
            System.out.println("error Receiver - Receiver [" + e.getMessage() + "]");
        }
    }

    @Override
    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(FFSync.getPORT());
            while (running) {                                           // infinite loop - very bad pratice
                byte[] inBuffer = new byte[MTU];
                // create the packet to receive the data from client
                DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
                serverSocket.receive(inPacket);
                DatagramSocket socket = new DatagramSocket(inPacket.getPort(), inPacket.getAddress());
                Thread t = new Thread(new RequestHandler(socket));
                t.start();
            }
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("erro receiver - run [" + e.getMessage() + "]");
        }
    }
}
