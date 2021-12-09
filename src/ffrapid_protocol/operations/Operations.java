package ffrapid_protocol.operations;

import ffrapid_protocol.exceptions.NotAckPacket;

import java.io.IOException;

public interface Operations {

    /**
     * Sends the data requested
     * @param data a get object
     */
    void sendData(byte[] data) throws IOException, NotAckPacket;
}
