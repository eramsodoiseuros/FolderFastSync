package ffrapid_protocol.data_packet_types;

import ffrapid_protocol.Packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Get extends Packet {
    private final static byte opcode = 0;

    private final boolean metadata; // Request for metadata
    private final List<String> filesName; // Name of the files
    private final boolean root; // All files of the root

    public Get() {
        metadata = false;
        filesName = null;
        root = false;
    }

    public Get(boolean metadata, boolean root) {
        this.metadata = metadata;
        this.root = root; // Returns all metadata of the root or all files of the root
        this.filesName = null;
    }

    public Get(boolean metadata, List<String> filesName) {
        this.metadata = metadata;
        this.filesName = filesName;
        this.root = false;
    }

    @Override
    public byte[] serialize() {
        int size = root ? 0 : 4 + filesName.stream().mapToInt(String::length).sum();
        ByteBuffer bb = ByteBuffer.allocate(4 + 1 + 1 + 4 + size); // opcode + boolean + boolean + size
        bb.putInt(opcode);
        bb.put((byte) (metadata ? 0 : 1));
        bb.put((byte) (root ? 0 : 1));
        if (!root) {
            bb.putInt(filesName.size());
            for (String str : filesName) {
                bb.putInt(str.length());
                bb.put(str.getBytes(StandardCharsets.UTF_8));
            }
        }
        return bb.array();
    }

    public static Packet deserialize(ByteBuffer byteBuffer) {
        Get get;
        List<String> list = new ArrayList<>();
        boolean metadata = byteBuffer.get() != 0;
        boolean root = byteBuffer.get() != 0;

        if (root) get = new Get(metadata, true);
        else {
            int len = byteBuffer.getInt();

            for (int i = 0; i < len; i++) {
                int strLen = byteBuffer.getInt();
                byte[] str = new byte[strLen];
                byteBuffer.get(str, 0, strLen);
                list.add(new String(str));
            }
            get = new Get(metadata, list);
        }
        return get;
    }
}
