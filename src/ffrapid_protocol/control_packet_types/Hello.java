package ffrapid_protocol.control_packet_types;

import ffrapid_protocol.ControlPacket;

import java.nio.ByteBuffer;

public class Hello extends ControlPacket {

    @Override
    public byte[] serializeType(ByteBuffer byteBuffer) {
        byteBuffer.putInt(0);

        return byteBuffer.array();
    }

    public static Hello deserialize(ByteBuffer byteBuffer) {
        return new Hello();
    }
}
