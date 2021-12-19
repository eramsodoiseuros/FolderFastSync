package ffrapid_protocol.packet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public interface Packet {

    /**
     * Deserializes the data received from the byte buffer.
     * @param byteBuffer the byte buffer.
     * @param type the type of the data.
     * @return the packet deserialized.
     */
    static Packet deserialize(ByteBuffer byteBuffer, int type) {
        return switch (type) {
            case 0 -> Get.deserialize(byteBuffer);
            case 1 -> Metadata.deserialize(byteBuffer);
            case 2 -> Error.deserialize(byteBuffer);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    /**
     * Returns the debugger level.
     * @return the debugger level.
     */
    static int getDebuggerLevel() {
        return 1;
    }

    /**
     * Returns the opcode used to identify this Packet.
     * @return the opcode.
     */
    byte getOpcode();

    /**
     * Serializes a Packet.
     *
     * @return A message to be sent.
     */
    byte[] serialize();

    /**
     * Handles the packet.
     */
    void handle(DatagramSocket socket, InetAddress address, int port) throws IOException;
}
