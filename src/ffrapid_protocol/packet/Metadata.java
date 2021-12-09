package ffrapid_protocol.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class Metadata extends Packet {
    private final static byte opcode = 3;
    public final List<AbstractMap.SimpleEntry<String, LocalDateTime>> metadata;

    public Metadata(List<AbstractMap.SimpleEntry<String, LocalDateTime>> metadata) {
        this.metadata = metadata;
    }

    public static Packet deserialize(ByteBuffer bb) {
        List<AbstractMap.SimpleEntry<String, LocalDateTime>> list = new ArrayList<>();

        int len = bb.getInt();

        for (int i = 0; i < len; i++) {
            int strLen = bb.getInt();
            byte[] str = new byte[strLen];
            bb.get(str, 0, strLen); // Gets the string
            // bb.get(); // Gets the LocalDateTime
            var pair = new AbstractMap.SimpleEntry<String, LocalDateTime>(new String(str), null);
            list.add(pair);
        }
        return new Metadata(list);
    }

    @Override
    public byte[] serialize() {
        int localDateTimeSize = 12;
        int listSize = metadata.stream().mapToInt(e -> e.getKey().length() + localDateTimeSize).sum();
        ByteBuffer bb = ByteBuffer.allocate(1 + 4 + listSize); // Opcode + ListSize + Sum(ElementSize + Element)
        bb.put(opcode);
        for (var item : metadata) {
            bb.putInt(item.getKey().length()); // String size
            bb.put(item.getKey().getBytes(StandardCharsets.UTF_8));
            //bb.put(item.getValue().); // LocalDateTime
        }
        return bb.array();
    }

}
