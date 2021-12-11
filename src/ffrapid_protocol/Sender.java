package ffrapid_protocol;

import app.FFSync;
import common.Node;
import common.Timer;
import ffrapid_protocol.data.StopAndWait;
import ffrapid_protocol.packet.Get;
import ffrapid_protocol.packet.Metadata;

import java.io.File;
import java.io.FileOutputStream;
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
            final InetAddress address = n.getAddress();
            int port = FFSync.getPORT();

            log("Sender | First node ip: " + address);

            Metadata metadata = requestsAllMetadata(socket, address, port);

            log("Sender | Comparing the files...");
            // Comparing the files...

            log("Sender | Updating the missing changes...");
            // Requesting the files
            // Getting the files...

            log("Sender | Nodes synchronized");

            // Requesting a file
            String fileName = "folder1/file1";
            requestFile(fileName, socket, address, port);

            // Download the file
            receiveFile(fileName, socket, address);


        } catch (Exception e) {
            e.printStackTrace();
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

    public void receiveFile(String fileName, DatagramSocket socket, InetAddress address) throws IOException {
        File f = new File(fileName + "2");
        FileOutputStream outputStream = new FileOutputStream(f);

        Timer.startTimer();
        StopAndWait.receiveFile(outputStream, socket, address);
        log("StopAndWait | File downloaded in " + Timer.getMiliseconds() + "ms");

        outputStream.close();
    }

    public void requestFile(String fileName, DatagramSocket socket, InetAddress address, int port) throws IOException {
        Get get = new Get(fileName);
        FTRapid.send(get, socket, address, port);
    }
}
