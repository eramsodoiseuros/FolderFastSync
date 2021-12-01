import java.net.DatagramSocket;

public class RequestHandler implements Runnable{
    private DatagramSocket socket;

    public RequestHandler(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

    }
}
