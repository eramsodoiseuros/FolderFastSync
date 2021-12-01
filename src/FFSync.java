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

 $ FFSync pasta1 10.3.3.1
 FFSync <pastas a sincronizar> <endereço de IP do sistema a sincronizar>
 FFSync <folder> <peer>


 Dps de executar o comando:
 1. Validar os parâmetros
 2. Verificar se há condições de funcionamento (Se há acesso à pasta e à rede).
 3. Começar a atender pedidos em TCP e UDP em simultâneo, ambos na porta 80.
 Usar outra porta sem ser a 80 no inicio para testar. Por exemplo, porta 8888.

 */

public class FFSync {
    private static final int MTU = 1500;
    private static final int PORT = 8888; // Port used to access, should be 80

    private final File currentDirectory;
    private final List<Node> nodes = new ArrayList<>(); // Connected nodes.

    private final Lock lock = new ReentrantLock();

    // metadados dos ficheiros - ou estrutura equivalente

    public static int getMTU() {
        return MTU;
    }

    public static int getPORT() {
        return PORT;
    }

    public List<Node> getNodes() {
        lock.lock();
        try {
            return new ArrayList<>(nodes);
        } finally {
            lock.unlock();
        }
    }

    public FFSync(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public void addNode(Node node) {
        lock.lock();
        try {
            nodes.add(node);
        } finally {
            lock.unlock();
        }
    }

    private void start() {
        // Creating 2 threads: To receive and to send requests - FFRapid protocol
        Thread sender = new Thread(new Sender(this));
        Thread receiver = new Thread(new Receiver(this));

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


    public static void main(String[] args) throws UnknownHostException {

        // Validating the arguments
        if (args.length < 1) System.exit(1); // Invalid number of arguments

        File directory = new File(args[0]);
        //if (!directory.exists()) System.exit(2); // Directory do not exist

        FFSync ffSync = new FFSync(directory);

        for (int i = 1; i < args.length; i++) ffSync.addNode(new Node(args[i]));

        ffSync.start();

    }
}

