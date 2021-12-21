package ffrapid_protocol.packet;

import compression.Compression;
import encryption.Encryption;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import static common.debugger.Debugger.log;
import static ffrapid_protocol.FTRapid.send;

public abstract class Packet {
    protected static final int debuggerLevel = 3;

    /**
     * Converts a message to a Packet.
     * @param message a message received.
     * @return a Packet converted from the message.
     */
    public static Packet deserialize(byte[] message) {
        ByteBuffer bb = ByteBuffer.wrap(message);
        byte[] data = new byte[bb.getInt()];
        System.arraycopy(message, 4, data, 0, data.length);
        Packet packet;
        data = Compression.decompress(Encryption.decrypt(data));
        assert data != null;
        bb = ByteBuffer.wrap(data);
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

    public byte[] encryptedCompression() {
        byte[] ser = this.serialize();
        byte[] data = Encryption.encrypt(Compression.compress(ser));
        ByteBuffer bb = ByteBuffer.allocate(data.length + Integer.BYTES);
        bb.putInt(data.length);
        bb.put(data);
        log("Compression % is: " + ser.length + " | " + data.length);
        return bb.array();
    }

    /**
     * Serializes a Packet.
     * @return A message to be sent.
     */
    public abstract byte[] serialize();

    /**
     * Handles the packet.
     */
    public void handle(DatagramSocket socket , InetAddress address, int port) throws IOException {
        Error errorPacket = new Error();
        send(errorPacket, socket, address, port); // Sends an error message
    }
}
