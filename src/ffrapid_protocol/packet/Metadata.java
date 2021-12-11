package ffrapid_protocol.packet;

import folder_parser.FolderParser;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static common.debugger.Debugger.log;

/**
 * Metadata requested.
 */
public class Metadata extends Packet {
    private final static byte opcode = 3;
    public final List<AbstractMap.SimpleEntry<String, Long>> metadata;

    public static Metadata getMetadataFromNames(List <String> file_names) {
        return new Metadata(FolderParser.metadata(file_names));
    }

    public Metadata(List<AbstractMap.SimpleEntry<String, Long>> metadata) {
        this.metadata = metadata;
    }

    public static Packet deserialize(ByteBuffer bb) {
        log("Metadata | Starting deserializing", Packet.debuggerLevel);
        List<AbstractMap.SimpleEntry<String, Long>> list = new ArrayList<>();
        Metadata metadata;

        int len = bb.getInt();

        for (int i = 0; i < len; i++) {
            int strLen = bb.getInt();
            byte[] str = new byte[strLen];
            bb.get(str, 0, strLen); // Gets the string
            long time = bb.getLong(); // Gets the long
            var pair = new AbstractMap.SimpleEntry<>(new String(str, StandardCharsets.UTF_8), time);
            list.add(pair);
        }
        metadata = new Metadata(list);
        log("Metadata | Deserialize result: " + metadata, Packet.debuggerLevel);
        return metadata;
    }

    @Override
    public byte[] serialize() {
        log("Metadata | Starting serializing", Packet.debuggerLevel);
        log("Metadata | Before Serialize: " + this, Packet.debuggerLevel);
        int listSize = metadata.stream().mapToInt(e -> 4 + e.getKey().length() + 8).sum(); // StringSize + String + Long
        ByteBuffer bb = ByteBuffer.allocate(1 + 4 + listSize); // Opcode + ListSize + Sum(ElementSize + Element + Long)
        bb.put(opcode);
        bb.putInt(this.metadata.size());
        for (var item : metadata) {
            bb.putInt(item.getKey().length()); // String size
            bb.put(item.getKey().getBytes(StandardCharsets.UTF_8)); // String
            bb.putLong(item.getValue()); // Long
        }
        return bb.array();
    }

    @Override
    public String toString() {
        return "Metadata - " +
                "metadata=" + metadata;
    }
}
