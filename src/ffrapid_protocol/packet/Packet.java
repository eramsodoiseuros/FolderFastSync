package ffrapid_protocol.packet;

import app.FFSync;
import encryption.Encryption;
import ffrapid_protocol.exceptions.NoConnectionException;
import ffrapid_protocol.flow_control.StopAndWaitV2;
import hmac.HMac;
import hmac.PacketCorruptedException;

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
    public static Packet deserialize(byte[] message) throws PacketCorruptedException {
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
     * Decrypts a packet. Uses HMac for authentication and integrity of the message.
     *
     * @param packet the packet in an array of bytes.
     * @return the decrypted packet in an array of bytes.
     */
    public static byte[] decryptedPacket(byte[] packet) throws PacketCorruptedException {
        ByteBuffer bb = ByteBuffer.wrap(packet);
        int len = bb.getInt();
        assert len <= FFSync.getMTU() - 4;
        byte[] data = new byte[len];
        bb.get(data, 0, data.length);

        return HMac.confirmPacketHeader(Encryption.decrypt(data));
    }

    /**
     * Encrypts a packet. Uses HMac for authentication and integrity of the message.
     *
     * @return the serialized packet encrypted.
     */
    public byte[] encryptedPacket() {
        byte[] packet = this.serialize();
        byte[] encrypted = Encryption.encrypt(HMac.addHeaderHMAC(packet));
        assert encrypted.length <= FFSync.getMTU() - 4 : "Possible overflow avoided"; // Guaranties that the buffer does not overflow.
        ByteBuffer bb = ByteBuffer.allocate(encrypted.length + Integer.BYTES);
        bb.putInt(encrypted.length);
        bb.put(encrypted);
        log("Encrypt compression % is: " + encrypted.length +
                        " / " + packet.length + " = " + (double) encrypted.length / packet.length, debuggerLevel);
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
    public void handle(DatagramSocket socket, InetAddress address, int port) throws NoConnectionException {
        Error errorPacket = new Error();
        StopAndWaitV2.send(errorPacket, socket, address, port); // Sends an error message
    }
}
