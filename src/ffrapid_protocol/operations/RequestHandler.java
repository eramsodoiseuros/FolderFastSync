package ffrapid_protocol.operations;

import ffrapid_protocol.FTRapid;
import ffrapid_protocol.packet.Data;
import ffrapid_protocol.packet.Get;
import ffrapid_protocol.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static common.debugger.Debugger.log;

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
                sendData((Get) packet);
            } else if (packet instanceof Data dataPacket) { // Data Packet in the beginning of the connection does not make sense
                // Sends an error message
            } // else if (packet instanceof )
            else ;
            // DatagramPacket datagramPacket = new DatagramPacket(packetToSend, packetToSend.length, address, port);

            // socket.send(datagramPacket);
            log("Packet sent");
            // log("Port: " + datagramPacket.getPort());
        } catch (Exception e) {
            System.out.println("Error ffrapid_protocol.operation.RequestHandler run [" + e.getMessage() + "]");
        }
    }

    public void sendData(Get get) throws IOException {
        // Stop and wait algorithm
        // 1. Sends the file block
        // 2. Waits for the Ack
        // 3. Goes back into step 1 until there's no more blocks

        byte[] block = new byte[0];
        Data data = new Data(0, block);
        FTRapid.send(data, socket, address, port);

        Packet packet = FTRapid.receive(socket);
        //if (!(packet instanceof Ack)) Error
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
}
