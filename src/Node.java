import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/** Info about a computer that connected.
 *
 */
public class Node {
    private Inet4Address address;

    public Node(String address) throws UnknownHostException {
            this.address = (Inet4Address) InetAddress.getByName(address);
    }
}
