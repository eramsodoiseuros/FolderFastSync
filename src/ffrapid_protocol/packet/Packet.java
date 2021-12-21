package ffrapid_protocol.packet;

import encryption.Encryption;
import ffrapid_protocol.flow_control.StopAndWait;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import static common.debugger.Debugger.log;

public abstract class Packet {
    protected static final int debuggerLevel = 3;

    /**
     * Converts a message to a Packet.
     *
     * @param message a message received.
     * @return a Packet converted from the message.
     */
    public static Packet deserialize(byte[] message) {
        ByteBuffer bb = ByteBuffer.wrap(decryptedPacket(message));
        byte type = bb.get();
        Packet packet = switch (type) {
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

    /**
     * Decrypts a packet.
     *
     * @param packet the packet in an array of bytes.
     * @return the decrypted packet in an array of bytes.
     */
    public static byte[] decryptedPacket(byte[] packet) {
        ByteBuffer bb = ByteBuffer.wrap(packet);
        byte[] data = new byte[bb.getInt()];
        bb.get(data, 0, data.length);

        return Encryption.decrypt(data);
    }

    /**
     * Encrypts a packet.
     *
     * @return the serialized packet encrypted.
     */
    public byte[] encryptedPacket() {
        byte[] packet = this.serialize();
        byte[] encrypted = Encryption.encrypt(packet);
        ByteBuffer bb = ByteBuffer.allocate(encrypted.length + Integer.BYTES);
        bb.putInt(encrypted.length);
        bb.put(encrypted);
        log("Encrypt compression % is: " + packet.length + " / " + encrypted.length + " = " + (double) packet.length / encrypted.length,
                debuggerLevel);
        return bb.array();
    }

    /**
     * Serializes a Packet.
     *
     * @return A message to be sent.
     */
    public abstract byte[] serialize();

    /**
     * Handles the packet.
     */
    public void handle(DatagramSocket socket, InetAddress address, int port) {
        Error errorPacket = new Error();
        StopAndWait.send(socket, address, port, errorPacket); // Sends an error message
    }
}
