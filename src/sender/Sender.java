package sender;

import app.FFSync;
import common.Node;
import ffrapid_protocol.FTRapid;

import javax.imageio.plugins.tiff.FaxTIFFTagSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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

        log("Sender Node ip: " + n.getAddress().toString() );

        try {
            DatagramSocket clientSocket = new DatagramSocket();                         // creates a socket - port not define - system gives an available port

            // buffer to read from the console
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String lineFromConsole = reader.readLine();                                 // reading from the console

            while (!lineFromConsole.equalsIgnoreCase("quit")) {
                byte[] inBuffer = new byte[MTU];
                byte[] outBuffer;

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
