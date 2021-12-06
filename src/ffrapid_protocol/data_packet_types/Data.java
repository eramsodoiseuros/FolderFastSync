package ffrapid_protocol.data_packet_types;

import ffrapid_protocol.DataPacket;

public class Data extends DataPacket {
    private long blockNumber;
    private byte[] data;

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
