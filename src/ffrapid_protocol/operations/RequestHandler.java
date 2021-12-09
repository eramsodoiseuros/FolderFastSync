package ffrapid_protocol.operations;

import app.FFSync;
import ffrapid_protocol.FTRapid;
import ffrapid_protocol.exceptions.NotAckPacket;
import ffrapid_protocol.packet.Error;
import ffrapid_protocol.packet.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import static common.DivideData.getBlock;
import static common.DivideData.getLastBlock;
import static common.debugger.Debugger.log;
import static ffrapid_protocol.FTRapid.*;

public class RequestHandler implements Runnable, Operations {
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
        if (get.metadata) {

        } else {

        }
    }

    private void getModification() {

    }

    public void sendFile(String fileName) throws IOException, NotAckPacket {
        sendData(Files.readAllBytes(Paths.get(fileName)));
    }

    public void sendData(byte[] data) throws IOException, NotAckPacket {
        // Stop and wait algorithm
        // 0. Sends the amount of packets that is gonna need to download the file
        // 1. Sends the file block
        // 2. Waits for the Ack
        // 2.1 [Ack is not received in the RTT] -> Sends the file block again
        // 3. Goes back into step 1 until there's no more blocks


        int MTU = FFSync.getMTU();
        int blocks = data.length / MTU;
        int lastBlockLen = data.length % MTU;
        Ack ack;

        // Sends the amount of packets
        FTRapid.send(new Ack(blocks + 1), socket, address, port);

        // Gets the blocks
        for (int i = 0; i < blocks; i++) {
            Data dataPacket = getBlock(MTU, data, i);

            // Sends the packet
            FTRapid.send(dataPacket, socket, address, port);

            // Waits for the Ack
            if (receivesAck(socket, address, port).segmentNumber - 1 != i)
                i--;
        }
        // Gets the last block
        Data dataPacket = getLastBlock(lastBlockLen, data, blocks, MTU);

        // Sends the packet
        FTRapid.send(dataPacket, socket, address, port);

        // Waits for the Ack
        receivesAck(socket, address, port);

        // Ler ack
        // Mandar block

        // Accumulative algorithm
        // 1. Divide the File in blocks
        // 2. Send each n blocks and wait for an Ack
        // 3. See if until block number did it get to
        // 4. Start sending with the block number missing
        // 5. Optional receive Ack in the middle of the sending process
        // 6. Read this Ack to see what has to be sent
    }

    public void receiveFile(FileOutputStream outputStream, DatagramSocket socket) throws IOException {
        // Stop and wait algorithm
        try {
            int seqNumber = 0;
            long packets = receivesAck(socket, address, port).segmentNumber;
            Data data;

            for (int i = 0; i < packets + 1; i++) { // Last block included
                data = (Data) FTRapid.receive(socket); // Assuming that we will receive data

                outputStream.write(data.data); // Writes the data

                sendAck(socket, address, port, seqNumber); // Sends the Ack
            }

            outputStream.close();


        } catch (NotAckPacket e) {
            log(e.getMessage());
        }

    }
}
