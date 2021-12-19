package ffrapid_protocol;

import app.FFSync;
import common.debugger.Debugger;
import ffrapid_protocol.flow_control.FlowControl;
import ffrapid_protocol.flow_control.StopAndWait;
import ffrapid_protocol.operations.RequestHandler;
import ffrapid_protocol.packet.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Receives FFRapidProtocol.FTRapid data.
 */
public class Receiver implements Runnable {

    private DatagramSocket serverSocket;
    @SuppressWarnings({"FieldMayBeFinal", "CanBeFinal", "FieldCanBeLocal"})
    private boolean running = true;

    public Receiver() {
        try {
            this.serverSocket = new DatagramSocket(FFSync.getPORT()); // mudar isto dps
        } catch (SocketException e) {
            System.out.println("Error FTRapid_protocol.Receiver - FTRapid_protocol.Receiver [" + e.getMessage() + "]");
        }
    }

    @Override
    public void run() {
        try {
            while (running) { // infinite loop - very bad practice
                DatagramSocket socket = new DatagramSocket();
                FlowControl flowControl = new StopAndWait();
                Packet received = flowControl.receive(socket);

                // Debugger.log("Receiver | New connection with address: " + received.getAddress() + " and port " + received.getPort());
                Thread t = new Thread(new RequestHandler(socket, received.getAddress(), received.getPort(), received));
                t.start();
            }
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("Error receiver - run [" + e.getMessage() + "]");
        }
    }
}
