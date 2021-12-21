package ffrapid_protocol.packet;

import java.nio.ByteBuffer;

import static common.debugger.Debugger.log;

/**
 * Acknowledges that a packet has been received.
 */
public class Ack extends Packet {
    private final static byte opcode = 2;

    public final int segmentNumber;

    public Ack(int segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    public byte[] serialize() {
        log("Ack | Starting serializing", Packet.debuggerLevel);
        log("Ack | Before Serialize: " + this, Packet.debuggerLevel);
        ByteBuffer bb = ByteBuffer.allocate(1 + 8);
        bb.put(opcode);
        bb.putLong(segmentNumber);
        return bb.array();
    }

    public static Packet deserialize(ByteBuffer byteBuffer) {
        log("Ack | Starting deserializing", Packet.debuggerLevel);

        Ack ack = new Ack(byteBuffer.getInt());

        log("Ack | Deserialize result: " + ack, Packet.debuggerLevel);

        return ack;
    }

    @Override
    public String toString() {
        return "Ack - " +
                "segmentNumber=" + segmentNumber;
    }
}
