package app;

import common.Node;
import common.debugger.Debugger;
import ffrapid_protocol.Receiver;
import ffrapid_protocol.Sender;
import http.ServerHandler;

import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * PROTOCOLO DE SINCRONIZAÇÃO
 * <p>
 * Objetivo:
 * - no protocolo devem estar definidas medidas para o comportamento da sincronização - falta
 * - os metodos de sincronização devem ter devidas regras estipuladas (tempo + processo) - falta
 * - o protocolo de sincronização faz recurso do protocolo de transferência de dados - falta
 * -
 * Algoritmos de peer2peer:
 * - Chord
 * - Kademlia
 * - Tapestry
 * - Pastry
 * <p>
 * $ app.FFSync pasta1 10.3.3.1
 * app.FFSync <pastas a sincronizar> <endereço de IP do sistema a sincronizar>
 * app.FFSync <folder> <peer>
 * <p>
 * <p>
 * Dps de executar o comando:
 * 1. Validar os parâmetros
 * 2. Verificar se há condições de funcionamento (Se há acesso à pasta e à rede).
 * 3. Começar a atender pedidos em TCP e UDP em simultâneo, ambos na porta 80.
 * Usar outra porta sem ser a 80 no inicio para testar. Por exemplo, porta 8888.
 */

public class FFSync {
    private static final int MTU = 1500 - 20 - 8; // 1500 - IP - UDP
    private static final int PORT = 12345; // Port used to access, should be 80
    private static final List<Node> nodes = new ArrayList<>(); // Connected nodes.
    private static final Lock lock = new ReentrantLock();
    private static File currentDirectory;

    public static int getMTU() {
        return MTU;
    }

    public static int getPORT() {
        return PORT;
    }

    public static List<Node> getNodes() {
        lock.lock();
        try {
            return new ArrayList<>(nodes);
        } finally {
            lock.unlock();
        }
    }

    public static void addNode(Node node) {
        lock.lock();
        try {
            nodes.add(node);
        } finally {
            lock.unlock();
        }
    }

    private static void start() {
        try {
            // Creating 2 threads: To receive and to send requests - FFRapid protocol
            Thread sender = new Thread(new Sender());
            Thread receiver = new Thread(new Receiver());

            // Creating a thread to handle the http request on port 80 - TCP/HTTP
            Thread serverHandler = new Thread(new ServerHandler());

            sender.start();
            receiver.start();
            serverHandler.start();

            sender.join();
            receiver.join();
            serverHandler.join();

            Debugger.close(); // Closing the debugger
        } catch (InterruptedException | SocketException e) {
            System.out.println("Error node - start [" + e.getMessage() + "]");
        }
    }

    public static File getCurrentDirectory() {
        lock.lock();
        try {
            return currentDirectory;
        } finally {
            lock.unlock();
        }
    }

    public static void setCurrentDirectory(File currentDirectory) {
        lock.lock();
        try {
            FFSync.currentDirectory = currentDirectory;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        Debugger.initialize(); // Initializing the debugger

        // Validating the arguments
        if (args.length < 1) {
            System.out.println("Invalid number of arguments");
            System.exit(1); // Invalid number of arguments
        }

        File directory = new File(args[0]);
        if (!directory.exists()) {
            System.out.println("Directory does not exist");
            System.exit(2); // Directory does not exist
        }

        FFSync.setCurrentDirectory(directory);

        for (int i = 1; i < args.length; i++) FFSync.addNode(new Node(args[i]));

        FFSync.start();

    }
}

