package ffrapid_protocol.data;

import app.FFSync;
import ffrapid_protocol.packet.Data;

import static common.debugger.Debugger.log;


public class DivideData {
    public final int blocks; // Number of blocks including the last one
    public final byte[] data;
    private final int MTU = FFSync.getMTU() - Data.headerLength;
    public final int lastBlockLen;

    public DivideData(byte[] data) {
        this.data = data;
        this.blocks = (this.data.length / MTU) + 1;
        this.lastBlockLen = this.data.length % MTU;
        log("DivideData | data length: " + data.length + ", blocks: " + blocks + ", lastBlockLength: " + lastBlockLen);
    }

    public byte[] getBlock(int blockSize, int blocks) {
        log("getBlock | blockSize: " + blockSize + ", blocks: " + blocks, 2);
        byte[] bytes = new byte[blockSize];
        System.arraycopy(data, blocks * MTU, bytes, 0, blockSize);
        return bytes;
    }

    public byte[] getBlock(int i) {
        if (i == blocks - 1) return getBlock(lastBlockLen, blocks); // Last block
        return getBlock(MTU, i);
    }
}
