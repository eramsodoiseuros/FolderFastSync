package FFRapidProtocol.ControlPacketTypes;

import FFRapidProtocol.ControlPacket;

import java.io.BufferedReader;
import java.nio.ByteBuffer;

public class ACK extends ControlPacket {
    private final long num_seq;

    public ACK(long num_seq) {
        this.num_seq = num_seq;
    }

    @Override
    public byte[] typeToBytes(ByteBuffer byteBuffer) {
        byteBuffer.putInt(5); // Number of ACK

        byteBuffer.putLong(num_seq);

        return byteBuffer.array();
    }


    public static ACK getFromBytes(ByteBuffer byteBuffer) {
        return new ACK(byteBuffer.getInt());
    }
}
