package ffrapid_protocol.packet;

import java.nio.ByteBuffer;

import static common.debugger.Debugger.log;

public class Data extends Packet {
    private final static byte opcode = 1;
    public final static int headerLength = Byte.BYTES + Long.BYTES + Integer.BYTES;

    public final long blockNumber;
    public final byte[] data;

    public Data(long blockNumber, byte[] data) {
        this.blockNumber = blockNumber;
        this.data = data;
    }

    public byte[] serialize() {
        ByteBuffer bb = ByteBuffer.allocate(headerLength + data.length);
        bb.put(opcode);
        bb.putLong(blockNumber);
        bb.putInt(data.length);
        bb.put(data);
        return bb.array();
    }

    public static Packet deserialize(ByteBuffer byteBuffer) {
        long blockNumber = byteBuffer.getLong();
        var len = byteBuffer.getInt();
        log("DataPacket | Length: " + len);
        byte[] arr = new byte[len];
        byteBuffer.get(arr);
        return new Data(blockNumber, arr);
    }
}
