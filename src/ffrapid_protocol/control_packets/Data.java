package ffrapid_protocol.control_packets;

import java.nio.ByteBuffer;

import static common.debugger.Debugger.log;

/**
 * Data from a file.
 */
public class Data extends ControlPacket {
    private final static byte opcode = 0;
    public final static int headerLength = Byte.BYTES + Long.BYTES + Integer.BYTES;

    public final long blockNumber;
    public final byte[] data;

    public Data(long blockNumber, byte[] data) {
        this.blockNumber = blockNumber;
        this.data = data;
    }

    @Override
    public byte getOpcode() {
        return opcode;
    }

    public byte[] serialize() {
        ByteBuffer bb = ByteBuffer.allocate(headerLength + data.length);
        bb.put(opcode);
        bb.putLong(blockNumber);
        bb.putInt(data.length);
        bb.put(data);
        return bb.array();
    }

    public static ControlPacket deserialize(ByteBuffer byteBuffer) {
        long blockNumber = byteBuffer.getLong();
        var len = byteBuffer.getInt();
        log("DataPacket | Length: " + len);
        byte[] arr = new byte[len];
        byteBuffer.get(arr);
        return new Data(blockNumber, arr);
    }
}
