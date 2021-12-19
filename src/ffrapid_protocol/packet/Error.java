package ffrapid_protocol.packet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Communicates that an error has occurred.
 */
public class Error implements Packet {
    private final static byte opcode = 4;
    private final static int SIZE = 0;
    // É preciso desenvolver

    public static Error deserialize(ByteBuffer bb) {
        return new Error();
    }

    @Override
    public byte getOpcode() {
        return opcode;
    }

    public byte[] serialize() {
        ByteBuffer bb = ByteBuffer.allocate(SIZE);
        return bb.array();
    }

    @Override
    public void handle(DatagramSocket socket, InetAddress address, int port) throws IOException {

    }
}
