package ffrapid_protocol.operations;

import app.FFSync;
import ffrapid_protocol.data.StopAndWait;
import ffrapid_protocol.exceptions.NotAckPacket;
import ffrapid_protocol.packet.Error;
import ffrapid_protocol.packet.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static common.debugger.Debugger.log;
import static ffrapid_protocol.FTRapid.send;

/**
 * Handles the requests received.
 */
public class RequestHandler implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;
    private final DatagramPacket initialPacket;

    public RequestHandler(DatagramSocket socket, InetAddress address, int port, DatagramPacket initialPacket) {
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.initialPacket = initialPacket;
    }

    @Override
    public void run() {
        try {
            Packet packet = Packet.deserialize(initialPacket.getData());

            if (packet instanceof Get) {
                parseGet((Get) packet);
            } else if (packet instanceof Data || packet instanceof Ack || packet instanceof Error) {
                // [Data | Ack | Error] Packet in the beginning of the connection does not make sense
                Error errorPacket = new Error();
                send(errorPacket, socket, address, port); // Sends an error message
            } else if (packet instanceof Notify) {
                getModification((Notify) packet);
            }
            log("Receiver | Packet sent");
        } catch (Exception e) {
            System.out.println("Error ffrapid_protocol.operation.RequestHandler run [" + e.getMessage() + "]");
            e.printStackTrace();
        }
    }

    /**
     * Parses the get packet. Executing and sending the requested operations.
     *
     * @param get a get packet.
     * @throws IOException an IOException.
     */
    private void parseGet(Get get) throws IOException {
        List<String> fileNames =
                get.root ? Arrays.stream(Objects.requireNonNull(FFSync.getCurrentDirectory().list())).toList() : get.filesName;
        assert fileNames != null;
        log("RequestHandler | parseGet fileNames: " + fileNames, 1);

        if (get.metadata) {
            Metadata metadata = Metadata.getMetadataFromNames(fileNames);
            send(metadata, socket, address, port);
        } else fileNames.forEach(this::sendFile);
    }

    /**
     * Performs the modification notified.
     *
     * @param notify a notify packet.
     */
    private void getModification(Notify notify) {

    }

    /**
     * Sends a file.
     *
     * @param fileName the name of the file.
     */
    public void sendFile(String fileName) {
        try {
            StopAndWait.sendData(socket, address, port, Files.readAllBytes(Paths.get(FFSync.getCurrentDirectory() + "/" + fileName)));
        } catch (NotAckPacket | IOException e) {
            e.printStackTrace();
        }
    }

}
