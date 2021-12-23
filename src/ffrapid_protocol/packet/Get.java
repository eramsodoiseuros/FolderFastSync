package ffrapid_protocol.packet;

import app.FFSync;
import ffrapid_protocol.data.files.FileOperations;
import ffrapid_protocol.exceptions.NoConnectionException;
import ffrapid_protocol.flow_control.StopAndWaitV2;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static common.debugger.Debugger.log;

/**
 * Requests metadata or files.
 */
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

    public Get(String fileName) {
        this.metadata = false;
        this.root = false;
        this.filesName = new ArrayList<>();
        this.filesName.add(fileName);
    }

    @Override
    public byte[] serialize() {
        log("Get | Starting serializing", Packet.debuggerLevel);
        log("Get | Before Serialize: " + this, Packet.debuggerLevel);
        int size;
        if (root) {
            size = 0;
        } else {
            assert filesName != null;
            size = 4 + filesName.stream().mapToInt(String::length).sum();
        }
        ByteBuffer bb = ByteBuffer.allocate(1 + 1 + 1 + 4 + size * (4)); // opcode + boolean + boolean + ListSize + Sum(ElementSize * Element)
        bb.put(opcode);
        bb.put((byte) (metadata ? 1 : 0));
        bb.put((byte) (root ? 1 : 0));
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
        log("Get | Starting deserializing", Packet.debuggerLevel);
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
        log("Get | Deserialize result: " + get, Packet.debuggerLevel);
        return get;
    }

    public List<File> getFiles() {
        assert this.filesName != null;
        return this.filesName.stream().map(File::new).collect(Collectors.toList());
    }

    @Override
    public void handle(DatagramSocket socket, InetAddress address, int port) throws NoConnectionException {
        parse(socket, address, port);
    }

    /**
     * Parses the get packet. Executing and sending the requested operations.
     *
     * @param socket a socket.
     * @param address a address.
     * @param port a port.
     */
    private void parse(DatagramSocket socket, InetAddress address, int port) throws NoConnectionException {
        List<String> fileNames =
                this.root ? Arrays.stream(Objects.requireNonNull(FFSync.getCurrentDirectory().list())).toList() : this.filesName;
        assert fileNames != null;
        log("Get | parseGet fileNames: " + fileNames, 1);

        if (this.metadata) {
            Metadata metadata = Metadata.getMetadataFromDirectory();
            StopAndWaitV2.send(metadata, socket, address, port);
        } else fileNames.forEach(f -> FileOperations.sendFile(f, socket, address, port));
    }


    @Override
    public String toString() {
        return "Get - " +
                "metadata=" + metadata +
                ", filesName=" + filesName +
                ", root=" + root;
    }
}
