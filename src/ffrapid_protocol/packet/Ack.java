package ffrapid_protocol.packet;

import java.nio.ByteBuffer;

/**
 * Acknowledges that a packet has been received.
 */
public class Ack extends Packet {
    private final static byte opcode = 2;

    public final long segmentNumber;

    public Ack(long segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    public byte[] serialize() {
        ByteBuffer bb = ByteBuffer.allocate(1 + 4);
        bb.put(opcode);
        bb.putLong(segmentNumber);
        return bb.array();
    }

    public static Packet deserialize(ByteBuffer byteBuffer) {
        return new Ack(byteBuffer.getLong());
    }

}
