package ffrapid_protocol.packet;

import java.nio.ByteBuffer;

/**
 * Communicates that an error has occurred.
 */
public class Error extends Packet {
    private final static byte opcode = 4;
    // É preciso desenvolver

    public static Packet deserialize(ByteBuffer bb) {
        return new Error();
    }

    @Override
    public byte[] serialize() {
        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.put(opcode);
        return bb.array();
    }
}
