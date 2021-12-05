package common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Info about a computer that connected.
 */
public class Node {
    private final InetAddress address;

    public Node(String address) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
    }

    public InetAddress getAddress() {
        return address;
    }
}
