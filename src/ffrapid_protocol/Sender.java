package ffrapid_protocol;

import app.FFSync;
import common.Node;
import common.Timer;
import ffrapid_protocol.data.StopAndWait;
import ffrapid_protocol.packet.Get;
import ffrapid_protocol.packet.Metadata;
import folder_parser.FolderParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static common.debugger.Debugger.log;

/**
 * Sends FFRapidProtocol.FTRapid packets.
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
            List<String> filesNeeded = compareMetadata(metadata);
            // Comparing the files...

            log("Sender | Updating the missing changes...");
            // Requesting the files
            //requestFiles(filesNeeded, socket, address, port);
            // Getting the files...
            //receiveFiles(filesNeeded, socket, address);

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

    /**
     * Requests all metadata from the directory.
     *
     * @param socket  a socket.
     * @param address a address.
     * @param port    a port.
     * @return The metadata packet received.
     * @throws IOException an IOException.
     */
    public Metadata requestsAllMetadata(DatagramSocket socket, InetAddress address, int port) throws IOException {
        Get get = new Get(true, true); // Metadata from all files

        FTRapid.send(get, socket, address, port); // Sends the request

        log("Sender | Packet sent");

        Metadata metadata = (Metadata) FTRapid.receive(socket); // Receives the response

        log("Sender | Packet received");

        return metadata;
    }

    /**
     * Compares the metadata received. Indicates the different files.
     * @param metadata a metadata packet.
     * @return The list of name files that are different.
     */
    private List<String> compareMetadata(Metadata metadata) {
        var files = Arrays.stream(Objects.requireNonNull(FFSync.getCurrentDirectory().list())).toList();
        var filesMeta = FolderParser.metadata(files);
        //metadata.metadata.stream().map();
        return null;
    }

    /**
     * Receives a file.
     *
     * @param fileName the name of the file.
     * @param socket   the socket to receive the file.
     * @param address  the address that is going to send the file.
     * @throws IOException an IOException
     */
    public void receiveFile(String fileName, DatagramSocket socket, InetAddress address) throws IOException {
        File f = new File(fileName + "2");
        FileOutputStream outputStream = new FileOutputStream(f);

        Timer.startTimer();
        StopAndWait.receiveFile(outputStream, socket, address);
        log("StopAndWait | File downloaded in " + Timer.getMiliseconds() + "ms");

        outputStream.close();
    }

    /**
     * Requests a file.
     *
     * @param fileName the name of the file.
     * @param socket   the socket.
     * @param address  an address.
     * @param port     a port.
     * @throws IOException an IOException.
     */
    public void requestFile(String fileName, DatagramSocket socket, InetAddress address, int port) throws IOException {
        Get get = new Get(fileName);
        FTRapid.send(get, socket, address, port);
    }

    public void requestFiles(List<String> fileNames, DatagramSocket socket, InetAddress address, int port) {
        fileNames.forEach(f -> {
            try {
                requestFile(f, socket, address, port);
            } catch (IOException e) {
                e.printStackTrace();
                log("Sender | Error requesting file: " + f);
            }
        });
    }

    public void receiveFiles(List<String> fileNames, DatagramSocket socket, InetAddress address) {
        fileNames.forEach(f -> {
            try {
                receiveFile(f, socket, address);
            } catch (IOException e) {
                e.printStackTrace();
                log("Sender | Error receiving file: " + f);
            }
        });
    }
}
