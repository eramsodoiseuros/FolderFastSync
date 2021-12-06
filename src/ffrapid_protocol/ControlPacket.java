package ffrapid_protocol;

import ffrapid_protocol.control_packet_types.Ack;
import ffrapid_protocol.control_packet_types.Hello;

import java.nio.ByteBuffer;

public abstract class ControlPacket extends Packet{
    protected ControlPacketType type;

    public ControlPacket() {
    }

    /**
     * 1 Byte para Mensagem de controlo ou Mensagem de Dados
     * m Bytes para tipo de Mensagem // m = 4
     * n Bytes para comprimento dos dados // n = data.length()
     */


    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1492); // MTU - UDPLength = 1500 - 8
        byteBuffer.put((byte) 1); // Control Byte
        return serializeType(byteBuffer);

    }

    public abstract byte[] serializeType(ByteBuffer byteBuffer);

    public static ControlPacket deserialize(ByteBuffer bb) {
        ControlPacket controlPacket;
        int integer = bb.getInt();
        switch (integer) {
            case 0:
                controlPacket = Hello.deserialize(bb);
                controlPacket.type = ControlPacketType.Hello;

            case 5:
                controlPacket = Ack.deserialize(bb);
                controlPacket.type = ControlPacketType.Ack;

            default:
                controlPacket = null;
        }

        return controlPacket;
    }

    public ControlPacketType getControlType() {
        return type;
    }
}
