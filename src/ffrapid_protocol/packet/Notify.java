package ffrapid_protocol.packet;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Notifies the nodes of a change.
 */
public class Notify extends Packet {
    private final static byte opcode = 4;
    public final Map<String, Long> changedFiles; // Changed files

    public Notify(Map<String, Long> changedFiles) {
        this.changedFiles = changedFiles;
    }

    public static Packet deserialize(ByteBuffer bb) {
        return new Notify(null);
    }

    @Override
    public byte[] serialize() {
        ByteBuffer bb = ByteBuffer.allocate(Byte.BYTES);
        bb.put(opcode);
        return bb.array();
    }

    @Override
    public void handle(DatagramSocket socket, InetAddress address, int port) {
        getModification(socket, address, port);
    }

    /**
     * Performs the modification notified.
     *
     * @param socket a socket.
     * @param address a address.
     * @param port a port.
     */
    private void getModification(DatagramSocket socket, InetAddress address, int port) {

    }

}
