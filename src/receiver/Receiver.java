package receiver;

import app.FFSync;
import common.RequestHandler;
import common.debugger.Debugger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * Receives FFRapidProtocol.FTRapid data.
 */
public class Receiver implements Runnable {
    private final int MTU = FFSync.getMTU();

    private final FFSync ffSync;
    private DatagramSocket serverSocket;
    private boolean running = true;

    public Receiver(FFSync ffSync) {
        this.ffSync = ffSync;
        try {
            this.serverSocket = new DatagramSocket(FFSync.getPORT()); // mudar isto dps
        } catch (SocketException e) {
            System.out.println("error receiver.Receiver - receiver.Receiver [" + e.getMessage() + "]");
        }
    }

    @Override
    public void run() {
        try {
            while (running) {                                           // infinite loop - very bad pratice
                System.out.println("Estou aqui");
                byte[] inBuffer = new byte[MTU];
                // create the packet to receive the data from client
                DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
                serverSocket.receive(inPacket);

                Debugger.print(new String(inPacket.getData(), StandardCharsets.UTF_8));
                Debugger.print("New connection with address: " + inPacket.getAddress() + " and port " + inPacket.getPort());
                DatagramSocket socket = new DatagramSocket();
                Thread t = new Thread(new RequestHandler(socket, inPacket.getAddress(), inPacket.getPort(), inPacket));
                t.start();
            }
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("erro receiver - run [" + e.getMessage() + "]");
        }
    }
}
