package ffrapid_protocol.data_packet_types;

import ffrapid_protocol.Packet;

import java.nio.ByteBuffer;

public class Hello extends Packet{ // Just says which port is used for the connection
    private final static byte opcode = 0;

    private final int port;

    public Hello(int port) {
        this.port = port;
    }

    public byte[] serialize() {
        ByteBuffer bb = ByteBuffer.allocate(1 + 4);
        bb.put(opcode);
        bb.putInt(port);
        return bb.array();
    }

    public static Packet deserialize(ByteBuffer byteBuffer) {
        return new Hello(byteBuffer.getInt());
    }

    public int getPort() {
        return port;
    }
}
