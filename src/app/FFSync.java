package app;

import common.Node;
import ffrapid_protocol.Receiver;
import ffrapid_protocol.Sender;

import java.io.File;
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

 $ app.FFSync pasta1 10.3.3.1
 app.FFSync <pastas a sincronizar> <endereço de IP do sistema a sincronizar>
 app.FFSync <folder> <peer>


 Dps de executar o comando:
 1. Validar os parâmetros
 2. Verificar se há condições de funcionamento (Se há acesso à pasta e à rede).
 3. Começar a atender pedidos em TCP e UDP em simultâneo, ambos na porta 80.
 Usar outra porta sem ser a 80 no inicio para testar. Por exemplo, porta 8888.

 */

public class FFSync {
    private static final int MTU = 1500 - 20 - 8; // 1500 - IP - UDP
    private static final int PORT = 12345; // Port used to access, should be 80

    private static File currentDirectory;
    private static final List<Node> nodes = new ArrayList<>(); // Connected nodes.

    private static final Lock lock = new ReentrantLock();

    // metadados dos ficheiros - ou estrutura equivalente

    public static int getMTU() {
        return MTU;
    }

    public static int getPORT() {
        return PORT;
    }

    public static void setCurrentDirectory(File currentDirectory) {
        lock.lock();
        try {
            FFSync.currentDirectory = currentDirectory;
        } finally {
            lock.unlock();
        }
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
        // Creating 2 threads: To receive and to send requests - FFRapid protocol
        Thread sender = new Thread(new Sender());
        Thread receiver = new Thread(new Receiver());

        // Creating a thread to handle the http request on port 80 - TCP/HTTP
        //Thread httpHandler = new Thread(new HttpHandler());

        sender.start();
        receiver.start();
        //httpHandler.start();

        try {
            sender.join();
            receiver.join();
            //httpHandler.join();
        } catch (InterruptedException e) {
            System.out.println("erro node - start [" + e.getMessage() + "]");
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

    public static void main(String[] args) throws UnknownHostException {

        // Validating the arguments
        if (args.length < 1) System.exit(1); // Invalid number of arguments

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

