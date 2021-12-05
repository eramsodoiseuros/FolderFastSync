package ffrapid_protocol;

import ffrapid_protocol.control_packet_types.Ack;
import ffrapid_protocol.control_packet_types.Hello;

import java.nio.ByteBuffer;

public abstract class ControlPacket {

    private TYPE type;
    private byte[] data;

    private enum TYPE {
        ACK
    }

    public ControlPacket() {
    }

    /**
     * 1 Byte para Mensagem de controlo ou Mensagem de Dados
     * m Bytes para tipo de Mensagem // m = 4
     * n Bytes para comprimento dos dados // n = data.length()
     */


    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1492); // MTU - UDPLength = 1500 - 8
        byteBuffer.put((byte) 1); // Control Byte
        return typeToBytes(byteBuffer);

    }

    public abstract byte[] typeToBytes(ByteBuffer byteBuffer);

    public static ControlPacket getFromBytes(byte[] message) {
        ControlPacket controlPacket;
        ByteBuffer bb = ByteBuffer.wrap(message);
        int type = bb.getInt();
        switch (type) {
            case 0:
                controlPacket = Hello.getFromBytes(bb);

            case 5:
                controlPacket = Ack.getFromBytes(bb);

            default:
                controlPacket = null;
        }

        return controlPacket;
    }

}
