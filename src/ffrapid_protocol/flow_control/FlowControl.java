package ffrapid_protocol.flow_control;

import ffrapid_protocol.packet.Packet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public interface FlowControl {

    /**
     * Sends the high-level packet.
     * @param packet the packet.
     * @param socket the socket.
     * @param address the address.
     * @param port the port.
     * @throws IOException an IOException.
     */
    void send(Packet packet, DatagramSocket socket, InetAddress address, int port) throws IOException;

    /**
     * Receives the high-level packet.
     * @param socket the socket.
     * @param address the address.
     * @param port the port.
     * @return the Packet received.
     * @throws IOException an IOException.
     */
    Packet receive(DatagramSocket socket) throws IOException;
}
