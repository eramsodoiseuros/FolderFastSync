package ffrapid_protocol.control_packet_types;

import ffrapid_protocol.ControlPacket;

import java.nio.ByteBuffer;

public class Ack extends ControlPacket {
    private final long num_seq;

    public Ack(long num_seq) {
        this.num_seq = num_seq;
    }

    @Override
    public byte[] serializeType(ByteBuffer byteBuffer) {
        byteBuffer.putInt(5); // Number of Ack

        byteBuffer.putLong(num_seq);

        return byteBuffer.array();
    }


    public static Ack deserialize(ByteBuffer byteBuffer) {
        return new Ack(byteBuffer.getInt());
    }
}
