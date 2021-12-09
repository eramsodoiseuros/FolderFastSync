package ffrapid_protocol.operations;

import app.FFSync;
import ffrapid_protocol.FTRapid;
import ffrapid_protocol.data.StopAndWait;
import ffrapid_protocol.exceptions.NotAckPacket;
import ffrapid_protocol.packet.Error;
import ffrapid_protocol.packet.*;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static common.debugger.Debugger.log;

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
                FTRapid.send(errorPacket, socket, address, port);
                // Sends an error message
            } else if (packet instanceof Notify) {
                getModification();
            }
            log("Packet sent");
        } catch (Exception e) {
            System.out.println("Error ffrapid_protocol.operation.RequestHandler run [" + e.getMessage() + "]");
        }
    }

    private void parseGet(Get get) {
        List<File> files = get.root ? List.of(Objects.requireNonNull(FFSync.getCurrentDirectory().listFiles())) :
                get.getFiles();
        if (get.metadata) {

        } else {
            files.forEach(file -> sendFile(file.getName()));
        }
    }

    private void getModification() {

    }

    public void sendFile(String fileName) {
        try {
            StopAndWait.sendData(socket, address, port, Files.readAllBytes(Paths.get(fileName)));
        } catch (NotAckPacket | IOException e) {
            e.printStackTrace();
        }
    }

}
