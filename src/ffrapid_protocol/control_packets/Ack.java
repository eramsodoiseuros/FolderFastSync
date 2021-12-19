package ffrapid_protocol.control_packets;

import java.nio.ByteBuffer;

import static common.debugger.Debugger.log;

/**
 * Acknowledges that a packet has been received.
 */
public class Ack extends ControlPacket {
    private final static byte opcode = 1;

    public final long segmentNumber;

    public Ack(long segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    public byte[] serialize() {
        log("Ack | Starting serializing", ControlPacket.debuggerLevel);
        log("Ack | Before Serialize: " + this, ControlPacket.debuggerLevel);
        ByteBuffer bb = ByteBuffer.allocate(1 + 8);
        bb.put(opcode);
        bb.putLong(segmentNumber);
        return bb.array();
    }

    public static ControlPacket deserialize(ByteBuffer byteBuffer) {
        log("Ack | Starting deserializing", ControlPacket.debuggerLevel);

        Ack ack = new Ack(byteBuffer.getLong());

        log("Ack | Deserialize result: " + ack, ControlPacket.debuggerLevel);

        return ack;
    }

    @Override
    public String toString() {
        return "Ack - " +
                "segmentNumber=" + segmentNumber;
    }

    @Override
    public byte getOpcode() {
        return opcode;
    }
}
