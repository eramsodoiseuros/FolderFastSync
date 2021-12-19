package ffrapid_protocol.data;

import app.FFSync;
import ffrapid_protocol.control_packets.Data;
import ffrapid_protocol.packet.Packet;

public class PacketToSegment {
    public final byte type;
    public final int blocks; // Number of blocks including the last one
    public final byte[] packet;
    private final int MTU = FFSync.getMTU() - Data.headerLength;
    public final int lastBlockLen;

    public PacketToSegment(Packet packet) {
        this.packet = packet.serialize();
        this.blocks = (this.packet.length / MTU) + 1;
        this.lastBlockLen = this.packet.length % MTU;
        this.type = packet.getOpcode();
    }

    public byte[] getBlock(int blockSize, int blocks) {
        byte[] bytes = new byte[blockSize];
        System.arraycopy(packet, blocks * MTU, bytes, 0, blockSize);
        return bytes;
    }

    public byte[] getBlock(int i) {
        if (i == blocks) return getBlock(lastBlockLen, blocks); // Last block
        return getBlock(MTU, i);
    }
}
