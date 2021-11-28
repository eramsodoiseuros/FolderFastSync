import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Receives FTRapid data.
 */
public class Receiver implements Runnable {
    private final int MTU = FFSync.getMTU();

    private final FFSync ffSync;
    private final DatagramSocket socket;
    private boolean running = true;

    public Receiver(FFSync ffSync) {
        this.ffSync = ffSync;
        this.socket = null; // mudar isto dps
    }

    @Override
    public void run() {
        try {
            DatagramSocket serveSocket = new DatagramSocket(FFSync.getPORT());
            while (running) {                                           // infinite loop - very bad pratice
                byte[] inBuffer = new byte[MTU];
                // create the packet to receive the data from client
                DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
                serveSocket.receive(inPacket);
                ClientHandler ch = new ClientHandler(inPacket);         // send received packet to new thread to be treated
                Thread t = new Thread(ch);
                t.start();
            }
            serveSocket.close();
        } catch (Exception e) {
            System.out.println("erro receiver - run [" + e.getMessage() + "]");
        }
    }
}
