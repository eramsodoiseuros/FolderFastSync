package ffrapid_protocol.data;

import app.FFSync;
import ffrapid_protocol.packet.Data;

import static common.debugger.Debugger.log;

public class DivideData {
    private final int debuggerLevel = 3;
    public final int blocks; // Number of blocks including the last one
    public final byte[] data;
    public final int maxBlockSize = ((FFSync.getMTU() - Data.headerLength - Integer.BYTES) / 16) * 16;
    public final int lastBlockLen;

    public DivideData(byte[] data) {
        this.data = data;
        this.blocks = (this.data.length / maxBlockSize) + 1;
        this.lastBlockLen = this.data.length % maxBlockSize;
        log("DivideData | data length: " + data.length + ", blocks: " + blocks + ", lastBlockLength: " + lastBlockLen, debuggerLevel);
    }

    public byte[] getBlock(int blockSize, int blocks) {
        log("getBlock | blockSize: " + blockSize + ", blocks: " + blocks, debuggerLevel);
        byte[] bytes = new byte[blockSize];
        System.arraycopy(data, (blocks - 1) * maxBlockSize, bytes, 0, blockSize);
        return bytes;
    }

    public byte[] getBlock(int i) {
        if (i == blocks) return getBlock(lastBlockLen, blocks); // Last block
        return getBlock(maxBlockSize, i);
    }
}
