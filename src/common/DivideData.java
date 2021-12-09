package common;

import ffrapid_protocol.packet.Data;

public class DivideData {

    public static Data getLastBlock(int lastBlockLen, byte[] data, int blocks, int MTU) {
        byte[] bytes = new byte[lastBlockLen];
        System.arraycopy(data, blocks * MTU, bytes, 0, lastBlockLen);
        return new Data(blocks, bytes);
    }

    public static Data getBlock(int MTU, byte[] file, int i) {
        return getLastBlock(MTU, file, i, MTU);
    }
}
