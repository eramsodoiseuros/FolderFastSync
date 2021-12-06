package ffrapid_protocol;

import ffrapid_protocol.data_packet_types.Get;

import java.nio.ByteBuffer;

public abstract class Packet {
    protected PacketType type;

    public abstract byte[] serialize();

    public static Packet deserialize(byte[] message) {
        Packet packet;
        ByteBuffer bb = ByteBuffer.wrap(message);
        byte control = bb.get();

        if (control == 0) {
            packet = ControlPacket.deserialize(bb);
            packet.type = PacketType.Control;
        }
        else if (control == 1) {
            packet = DataPacket.deserialize(bb);
            packet.type = PacketType.Data;
        }
        else packet = null;

        return packet;
    }

    public PacketType getType() {
        return type;
    }
}
