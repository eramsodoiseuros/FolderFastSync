package ffrapid_protocol.operations;

import ffrapid_protocol.packet.Get;

import java.io.IOException;

public interface Operations {

    /**
     * Sends the data requested
     * @param get a get object
     */
    void sendData(Get get) throws IOException;
}
