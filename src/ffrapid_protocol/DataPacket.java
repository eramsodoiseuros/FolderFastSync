package ffrapid_protocol;

import ffrapid_protocol.data_packet_types.Get;

import java.nio.ByteBuffer;

public abstract class DataPacket extends Packet {

    public abstract byte[] serialize();

    public static DataPacket deserialize(ByteBuffer byteBuffer) {
        DataPacket dataPacket;
        int type = byteBuffer.getInt();
        switch (type) {
            case 0: // GET
                dataPacket = Get.deserialize(byteBuffer);

            default:
                dataPacket = null;
        }

        return dataPacket;
    }
}
