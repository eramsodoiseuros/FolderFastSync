package ffrapid_protocol.flow_control;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class SlidingWindow {

    public static void sendData(DatagramSocket socket, InetAddress address, int port, byte[] data) {
        // Accumulative algorithm
        // 1. Divide the File in blocks
        // 2. Send each n blocks and wait for an Ack
        // 3. See if until block number did it get to
        // 4. Start sending with the block number missing
        // 5. Optional receive Ack in the middle of the sending process
        // 6. Read this Ack to see what has to be sent
    }

}
