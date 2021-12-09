package ffrapid_protocol.packet;

import java.nio.ByteBuffer;

/**
 * Notifies the nodes of a change.
 */
public class Notify extends Packet {
    private final static byte opcode = 3;
    // É preciso desenvolver

    public static Packet deserialize(ByteBuffer bb) {
        return new Notify();
    }

    @Override
    public byte[] serialize() {
        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.put(opcode);
        return bb.array();
    }
}
