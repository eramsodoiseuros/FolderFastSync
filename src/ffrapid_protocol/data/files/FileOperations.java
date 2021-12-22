package ffrapid_protocol.data.files;

import app.FFSync;
import common.Timer;
import compression.Compression;
import ffrapid_protocol.data.DivideData;
import ffrapid_protocol.exceptions.NoConnectionException;
import ffrapid_protocol.flow_control.StopAndWait;
import ffrapid_protocol.flow_control.StopAndWaitV2;
import ffrapid_protocol.packet.Get;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static common.debugger.Debugger.log;

public class FileOperations {
    private final DatagramSocket socket; // Socket to receive and request files.
    private final InetAddress address;
    private final int port = FFSync.getPORT();

    public FileOperations(DatagramSocket socket, InetAddress address) {
        this.socket = socket;
        this.address = address;
    }

    /**
     * Requests and downloads a list of files in parallel.
     *
     * @param address the address of the node.
     * @param files   list of the files to get.
     * @throws InterruptedException an InterruptedException.
     */
    public static void getFiles(Set<Map.Entry<String, Long>> files, InetAddress address) throws InterruptedException {
        // Defining the runnable for each thread
        Function<Map.Entry<String, Long>, Thread> convertToThread = file -> new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                FileOperations fo = new FileOperations(socket, address);
                fo.getFile(file);
            } catch (IOException | NoConnectionException e) {
                e.printStackTrace();
            }
        });

        var threads = files.stream().map(convertToThread).collect(Collectors.toSet());

        threads.forEach(Thread::start);

        for (Thread thread : threads) thread.join();
    }

    /**
     * Receives a file.
     *
     * @param fileName         the name of the file.
     * @param lastTimeModified the last time that the file was modified.
     * @throws IOException an IOException.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void receiveFile(String fileName, long lastTimeModified) throws IOException {
        File file = new File(FFSync.getCurrentDirectory() + "/" + fileName);

        Timer timer = new Timer();
        StopAndWait.receiveFile(file, socket, address);
        log("StopAndWait | File downloaded in " + timer.getMilliseconds() + "ms");

        file.setLastModified(lastTimeModified);
    }

    /**
     * Requests a file.
     *
     * @param fileName the name of the file.
     */
    public void requestFile(String fileName) throws NoConnectionException {
        Get get = new Get(fileName);
        StopAndWaitV2.send(get, socket, address, port);
    }

    /**
     * Requests a file and downloaded it.
     *
     * @param file the name of the file and the last time modified.
     */
    public void getFile(Map.Entry<String, Long> file) throws IOException, NoConnectionException {
        requestFile(file.getKey());
        receiveFile(file.getKey(), file.getValue());
    }

    public void sendFile(String fileName) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(FFSync.getCurrentDirectory() + "/" + fileName));
        data = Compression.compress(data);

        DivideData divideData = new DivideData(data);

    }

    public void receiveFile(File f) {

    }
}
