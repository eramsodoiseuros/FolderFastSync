package ffrapid_protocol.packet;

import java.nio.ByteBuffer;

import static common.debugger.Debugger.log;

public abstract class Packet {
    protected static final int debuggerLevel = 1;

    public static Packet deserialize(byte[] message) {
        Packet packet;
        ByteBuffer bb = ByteBuffer.wrap(message);
        byte type = bb.get();
        packet = switch (type) {
            case 0 -> // Get packet
                    Get.deserialize(bb);
            case 1 -> // Data packet
                    Data.deserialize(bb);
            case 2 -> // Ack packet
                    Ack.deserialize(bb);
            case 3 -> // Metadata packet
                    Metadata.deserialize(bb);
            case 4 -> // Notify packet
                    Notify.deserialize(bb);
            case 5 -> // Error packet
                    Error.deserialize(bb);
            default -> null;
        };
        log("Packet | Type: " + type, debuggerLevel);

        return packet;
    }

    public abstract byte[] serialize();
}
