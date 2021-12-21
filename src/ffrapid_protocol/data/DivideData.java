package ffrapid_protocol.data;

import app.FFSync;
import ffrapid_protocol.packet.Data;


public class DivideData {
    public final int blocks; // Number of blocks including the last one
    public final byte[] data;
    private final int MTU = FFSync.getMTU() - Data.headerLength;
    public final int lastBlockLen;

    public DivideData(byte[] data) {
        this.data = data;
        this.blocks = (this.data.length / MTU) + 1;
        this.lastBlockLen = this.data.length % MTU;
    }

    public byte[] getBlock(int blockSize, int blocks) {
        byte[] bytes = new byte[blockSize];
        System.arraycopy(data, blocks * MTU, bytes, 0, blockSize);
        return bytes;
    }

    public byte[] getBlock(int i) {
        if (i == blocks) return getBlock(lastBlockLen, blocks); // Last block
        return getBlock(MTU, i);
    }
}
