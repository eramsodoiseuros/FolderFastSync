package ffrapid_protocol.packet;

import java.nio.ByteBuffer;

public class Data extends Packet {
    private final static byte opcode = 1;

    public final long blockNumber;
    public final byte[] data;

    public Data(long blockNumber, byte[] data) {
        this.blockNumber = blockNumber;
        this.data = data;
    }

    public byte[] serialize() {
        ByteBuffer bb = ByteBuffer.allocate(1 + 8 + 4 + data.length);
        bb.put(opcode);
        bb.putLong(blockNumber);
        bb.putInt(data.length);
        bb.put(data);
        return bb.array();
    }

    public static Packet deserialize(ByteBuffer byteBuffer) {
        long blockNumber = byteBuffer.getLong();
        byte[] arr = new byte[byteBuffer.getInt()];
        byteBuffer.get(arr);
        return new Data(blockNumber, arr);
    }
}
