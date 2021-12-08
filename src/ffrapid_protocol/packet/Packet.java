package ffrapid_protocol.packet;

import java.nio.ByteBuffer;

public abstract class Packet {
    public abstract byte[] serialize();

    public static Packet deserialize(byte[] message) {
        Packet packet;
        ByteBuffer bb = ByteBuffer.wrap(message);
        byte type = bb.get();
        packet = switch (type) {
            case 0 -> // Hello packet
                    Hello.deserialize(bb);
            case 1 -> // Get packet
                    Get.deserialize(bb);
            case 2 -> // Data packet
                    Data.deserialize(bb);
            case 3 -> // Ack packet
                    null;
            case 4 -> // Error packet
                    null;
            default -> null;
        };

        return packet;
    }

}
