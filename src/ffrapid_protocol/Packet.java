package ffrapid_protocol;

import ffrapid_protocol.data_packet_types.Data;
import ffrapid_protocol.data_packet_types.Get;

import java.nio.ByteBuffer;

public abstract class Packet {
    public abstract byte[] serialize();

    public static Packet deserialize(byte[] message) {
        Packet packet;
        ByteBuffer bb = ByteBuffer.wrap(message);
        byte control = bb.get();
        switch (control) {
            case 0: // Get packet
                packet = Get.deserialize(bb);
                break;

            case 1: // Data packet
                packet = Data.deserialize(bb);
                break;

            case 2: // Ack packet
                packet = null;
                break;

            default:
                packet = null;
        }

        return packet;
    }

}
