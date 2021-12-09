package ffrapid_protocol.packet;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Get extends Packet {
    private final static byte opcode = 0;

    public final boolean metadata; // Request for metadata
    public final List<String> filesName; // Name of the files
    public final boolean root; // All files of the root

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

    public static byte getOpcode() {
        return opcode;
    }

    @Override
    public byte[] serialize() {
        int size;
        if (root) {
            size = 0;
        } else {
            assert filesName != null;
            size = 4 + filesName.stream().mapToInt(String::length).sum();
        }
        ByteBuffer bb = ByteBuffer.allocate(1 + 1 + 1 + 4 + size); // opcode + boolean + boolean + size
        bb.put(opcode);
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

    public List<File> getFiles() {
        assert this.filesName != null;
        return this.filesName.stream().map(File::new).collect(Collectors.toList());
    }
}
