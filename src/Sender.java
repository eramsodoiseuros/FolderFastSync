import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Sends FTRapid data.
 */

public class Sender implements Runnable {
    private final FFSync ffSync;
    private final int MTU = FFSync.getMTU();

    public Sender(FFSync ffSync) {
        this.ffSync = ffSync;
    }

    @Override
    public void run() {
        // Sends a request in the beginning of the program and with changes in the directory
        // https://docs.oracle.com/javase/tutorial/essential/io/notification.html
        Node n = ffSync.getNodes().get(0); // Vamos começar por uma ligação apenas

        System.out.println(n.getAddress());

        try {
            DatagramSocket clientSocket = new DatagramSocket();                         // creates a socket - port not define - system gives an available port

            // buffer to read from the console
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String lineFromConsole = reader.readLine();                                 // reading from the console

            while (!lineFromConsole.equalsIgnoreCase("quit")) {
                byte[] inBuffer = new byte[MTU];
                byte[] outBuffer = new byte[MTU];

                // from the console to the socket - sending a message
                outBuffer = lineFromConsole.getBytes();
                DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, n.getAddress(), FFSync.getPORT());
                clientSocket.send(outPacket);

                // from the socket to the console - reading a message
                DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
                clientSocket.receive(inPacket);
                System.out.println(new String(inPacket.getData()));

                lineFromConsole = reader.readLine();                                    // reading from console
            }
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("erro sender - run [" + e.getMessage() + "]");
        }
    }
}
