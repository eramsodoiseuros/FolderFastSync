package ffrapid_protocol.data_packet_types;

import ffrapid_protocol.Packet;

import java.nio.ByteBuffer;

public class Data extends Packet {
    private final static byte opcode = 1;

    private long blockNumber;
    private byte[] data;

    public Data(long blockNumber, byte[] data) {
        this.blockNumber = blockNumber;
        this.data = data;
    }

    public byte[] serialize() {
        ByteBuffer bb = ByteBuffer.allocate(4 + 8 + 4 + data.length);
        bb.putInt(opcode);
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

    public long getBlockNumber() {
        return blockNumber;
    }

    public byte[] getData() {
        return data;
    }
}
