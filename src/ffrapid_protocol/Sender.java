package ffrapid_protocol;

import app.FFSync;
import common.Node;
import common.Timer;
import ffrapid_protocol.data.files.FileOperations;
import ffrapid_protocol.flow_control.StopAndWait;
import ffrapid_protocol.packet.Get;
import ffrapid_protocol.packet.Metadata;
import folder_parser.FolderParser;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.function.Consumer;

import static common.debugger.Debugger.log;

/**
 * Sends FFRapidProtocol.FTRapid packets.
 */
public class Sender implements Runnable {
    private final DatagramSocket socket = new DatagramSocket(); // Creates a socket with an available port
    private final Node n = FFSync.getNodes().get(0); // Vamos começar por uma ligação apenas
    private final InetAddress address = n.getAddress();
    private final int port = FFSync.getPORT();
    private float RTT;

    public Sender() throws SocketException {
    }


    @Override
    public void run() {
        // Sends a request in the beginning of the program and with changes in the directory
        // https://docs.oracle.com/javase/tutorial/essential/io/notification.html

        try {
            log("Sender | First node ip: " + address);

            Timer timer = new Timer();
            Metadata metadata = requestsAllMetadata();
            RTT = timer.getMilliseconds() / 2f * 1.25f;

            log("Sender | Comparing the files...");
            var filesNeeded = compareMetadata(metadata);
            // Comparing the files...
            log("Sender | Different files: " + filesNeeded);

            log("Sender | Updating the missing changes...");

            FileOperations.getFiles(filesNeeded.getKey(), address); // Downloading the needed files...

            sendNeededFiles(filesNeeded.getValue()); // Informing the node the missing files on its side.

            log("Sender | Nodes synchronized");

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

        StopAndWait.send(socket, address, port, get); // Sends the request
        log("Sender | Packet sent");

        Metadata metadata = (Metadata) StopAndWait.receive(socket); // Receives the response
        log("Sender | Packet received");

        return metadata;
    }

    /**
     * Compares the metadata received. Indicates the different files.
     *
     * @param metadata a metadata packet.
     * @return The list of name files that are different.
     */
    private Map.Entry<Set<Map.Entry<String, Long>>, Map<String, Long>> compareMetadata(Metadata metadata) {
        var files = Objects.requireNonNull(FFSync.getCurrentDirectory().list());
        Map<String, Long> filesMeta = FolderParser.metadata(Arrays.stream(files).toList());
        // Metadata from the local node ^

        // Files to be requested
        Set<Map.Entry<String, Long>> local = new HashSet<>();
        Map<String, Long> remote = new HashMap<>();

        Consumer<Map.Entry<String, Long>> different = e -> {local.add(e); /*
            Long time = filesMeta.remove(e.getKey());
            if (time == null) local.add(e); // Local File does exist
            else if (e.getValue().equals(time)); // Files are equal.
            else if (localFileChange(e.getValue(), time)) local.add(e);
            else remote.put(e.getKey(), time); */
        };

        metadata.metadata.entrySet().forEach(different); // Compares the files between the remote node and the local.
        remote.putAll(filesMeta); // Adds the rest of file that the remote node does have.

        log("Sender | Files to be requested by the local node: " + local);
        log("Sender | Files to be requested by the remote node: " + remote);
        return new AbstractMap.SimpleEntry<>(local, remote);
    }

    /**
     * Determines if the local node has to change.
     *
     * @param remote the time of the remote file.
     * @param local the time of the local file.
     * @return true if the local file is the one to be changed.
     */
    private boolean localFileChange(Long remote, Long local) {
        return remote > local; // if the remote file in more recent the localFile changes
    }

    /**
     * Sends the metadata from the files that the other node is going to need.
     *
     * @param files metadata from the files.
     */
    private void sendNeededFiles(Map<String, Long> files) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        Metadata metadata = new Metadata(files);
        StopAndWait.send(socket, address, port, metadata);
    }
}
