package ffrapid_protocol.packet;

import ffrapid_protocol.data.files.FileOperations;
import folder_parser.FolderParser;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static common.debugger.Debugger.log;

/**
 * Metadata requested.
 */
public class Metadata implements Packet {
    private final static byte opcode = 1;
    public final Map<String, Long> metadata; // File name -> time

    public Metadata(Map<String, Long> metadata) {
        this.metadata = metadata;
    }

    public static Metadata getMetadataFromNames(List<String> file_names) {
        return new Metadata(FolderParser.metadata(file_names));
    }

    public static Metadata deserialize(ByteBuffer bb) {
        log("Metadata | Starting deserializing", Packet.getDebuggerLevel());
        Map<String, Long> map = new HashMap<>();
        Metadata metadata;

        int len = bb.getInt();

        for (int i = 0; i < len; i++) {
            int strLen = bb.getInt();
            byte[] str = new byte[strLen];
            bb.get(str, 0, strLen); // Gets the string
            long time = bb.getLong(); // Gets the long
            map.put(new String(str, StandardCharsets.UTF_8), time);
        }
        metadata = new Metadata(map);
        log("Metadata | Deserialize result: " + metadata, Packet.getDebuggerLevel());
        return metadata;
    }

    public byte getOpcode() {
        return opcode;
    }

    public byte[] serialize() {
        log("Metadata | Starting serializing", Packet.getDebuggerLevel());
        log("Metadata | Before Serialize: " + this, Packet.getDebuggerLevel());
        int stringsSize = metadata.keySet().stream().mapToInt(String::length).sum();
        int headerLen = 1 + 4 + stringsSize + (4 + 8) * metadata.size(); // Opcode + ListSize + Sum(ElementSize + Element + Long)
        ByteBuffer bb = ByteBuffer.allocate(headerLen);
        bb.put(opcode);
        bb.putInt(this.metadata.size());
        for (var item : metadata.entrySet()) {
            bb.putInt(item.getKey().length()); // String size
            bb.put(item.getKey().getBytes(StandardCharsets.UTF_8)); // String
            bb.putLong(item.getValue()); // Long
        }
        return bb.array();
    }

    @Override
    public void handle(DatagramSocket socket, InetAddress address, int port) {
        // This metadata is for the file that are missing in this node.
        // I have to request and download these files.
        try {
            FileOperations.getFiles(metadata.entrySet(), address);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "Metadata - " +
                "metadata=" + metadata;
    }
}
