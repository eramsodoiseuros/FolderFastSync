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
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static common.debugger.Debugger.log;

/**
 * Sends FFRapidProtocol.FTRapid packets.
 */
public class Sender implements Runnable {
    private final DatagramSocket socket = new DatagramSocket(); // Creates a socket with an available port
    private final Node n = FFSync.getNodes().get(0); // Vamos começar por uma ligação apenas
    private final InetAddress address = n.getAddress();
    private final int port = FFSync.getPORT();

    public Sender() throws SocketException {
    }


    @Override
    public void run() {
        // Sends a request in the beginning of the program and with changes in the directory
        // https://docs.oracle.com/javase/tutorial/essential/io/notification.html

        try {

            log("Sender | First node ip: " + address);

            Metadata metadata = requestsAllMetadata();

            log("Sender | Comparing the files...");
            List<Map.Entry<String, Long>> filesNeeded = compareMetadata(metadata);
            // Comparing the files...
            log("Sender | Different files: " + filesNeeded);

            log("Sender | Updating the missing changes...");
            // Downloading the needed files...
            for (var file : filesNeeded) getFile(file);

            log("Sender | Nodes synchronized");

            // Requesting a file
            // String fileName = "folder1/file1";
            // requestFile(fileName);

            // Download the file
            // receiveFile(fileName);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Requests all metadata from the directory.
     *
     * @return The metadata packet received.
     * @throws IOException an IOException.
     */
    public Metadata requestsAllMetadata() throws IOException {
        Get get = new Get(true, true); // Metadata from all files

        FTRapid.send(get, socket, address, port); // Sends the request

        log("Sender | Packet sent");

        Metadata metadata = (Metadata) FTRapid.receive(socket); // Receives the response

        log("Sender | Packet received");

        return metadata;
    }

    /**
     * Compares the metadata received. Indicates the different files.
     *
     * @param metadata a metadata packet.
     * @return The list of name files that are different.
     */
    private List<Map.Entry<String, Long>> compareMetadata(Metadata metadata) {
        var files = Objects.requireNonNull(FFSync.getCurrentDirectory().list());
        Map<String, Long> filesMeta = FolderParser.metadata(Arrays.stream(files).toList());
        Predicate<Map.Entry<String, Long>> different = e -> {
            Long time = filesMeta.get(e.getKey());
            return !Objects.equals(time, e.getValue()) || time == null;
        };
        log("Local files: " + filesMeta);
        log("Other files: " + metadata.metadata);
        return metadata.metadata.entrySet().stream().filter(different).collect(Collectors.toList());
    }

    /**
     * Receives a file.
     *
     * @param fileName the name of the file.
     * @param lastTimeModified the last time that the file was modified.
     * @throws IOException an IOException.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void receiveFile(String fileName, long lastTimeModified) throws IOException {
        File f = new File(FFSync.getCurrentDirectory() + "/" + fileName);
        FileOutputStream outputStream = new FileOutputStream(f);

        Timer.startTimer();
        StopAndWait.receiveFile(outputStream, socket, address);
        log("StopAndWait | File downloaded in " + Timer.getMiliseconds() + "ms");

        outputStream.close();
        f.setLastModified(lastTimeModified);
    }

    /**
     * Requests a file.
     *
     * @param fileName the name of the file.
     * @throws IOException an IOException.
     */
    public void requestFile(String fileName) throws IOException {
        Get get = new Get(fileName);
        FTRapid.send(get, socket, address, port);
    }

    /**
     * Requests a file and downloaded it.
     * @param file the name of the file and the last time modified.
     */
    public void getFile(Map.Entry<String, Long> file) throws IOException {
        requestFile(file.getKey());
        receiveFile(file.getKey(), file.getValue());
    }
}
