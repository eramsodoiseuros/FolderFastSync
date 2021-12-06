package ffrapid_protocol.data_packet_types;

import ffrapid_protocol.DataPacket;

import java.nio.ByteBuffer;
import java.util.List;

public class Get extends DataPacket {
    private final boolean metadata; // Request for metadata
    private final List<String> filesName; // Name of the files
    private final boolean root; // All files of the root

    public Get() {
        metadata = false;
        filesName = null;
        root = false;
    }

    public Get(boolean metadata) {
        this.metadata = metadata;
        this.root = metadata; // Returns all metadata of the root or all files of the root
        this.filesName = null;
    }

    public Get(boolean metadata, List<String> filesName) {
        this.metadata = metadata;
        this.filesName = filesName;
        this.root = false;
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }

    public static DataPacket deserialize(ByteBuffer byteBuffer) {
        Get get = new Get();


        return dataPacket;
    }
}
