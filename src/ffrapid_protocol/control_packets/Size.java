package ffrapid_protocol.control_packets;

import java.nio.ByteBuffer;

import static common.debugger.Debugger.log;

public class Size extends ControlPacket {
    private final static byte opcode = 2;
    private final static int SIZE = 2 * Byte.BYTES + Integer.BYTES;
    public final byte type;
    public int sequenceNumber;
    public final int size;

    public Size(byte type, int sequenceNumber, int size) {
        this.type = type;
        this.sequenceNumber = sequenceNumber;
        this.size = size;
    }

    public static ControlPacket deserialize(ByteBuffer byteBuffer) {
        log("Size | Starting deserializing", ControlPacket.debuggerLevel);

        Size size = new Size(byteBuffer.get(), byteBuffer.getInt(), byteBuffer.getInt());

        log("Size | Deserialize result: " + size, ControlPacket.debuggerLevel);

        return size;
    }

    @Override
    public byte getOpcode() {
        return opcode;
    }

    public byte[] serialize() {
        log("Size | Starting serializing", ControlPacket.debuggerLevel);
        log("Size | Before Serialize: " + this, ControlPacket.debuggerLevel);
        ByteBuffer bb = ByteBuffer.allocate(SIZE);

        bb.put(opcode);
        bb.put(type);
        bb.putInt(sequenceNumber);
        bb.putInt(size);

        return bb.array();
    }
}
