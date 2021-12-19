package ffrapid_protocol.control_packets;

import java.nio.ByteBuffer;

import static common.debugger.Debugger.log;

public abstract class ControlPacket {
    protected static final int debuggerLevel = 3;

    /**
     * Converts a message to a ControlPacket.
     * @param message a message received.
     * @return a ControlPacket converted from the message.
     */
    public static ControlPacket deserialize(byte[] message) {
        ControlPacket packet;
        ByteBuffer bb = ByteBuffer.wrap(message);
        byte type = bb.get();
        packet = switch (type) {
            case 0 -> // Data packet
                    Data.deserialize(bb);
            case 1 -> // Ack packet
                    Ack.deserialize(bb);
            case 2 -> // Size packet
                    Size.deserialize(bb);
            default -> null;
        };
        log("ControlPacket | Type: " + type, debuggerLevel);

        return packet;
    }

    public abstract byte getOpcode();

    /**
     * Serializes a ControlPacket.
     * @return A message to be sent.
     */
    public abstract byte[] serialize();
}
