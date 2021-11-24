import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 *  PROTOCOLO DE SINCRONIZAÇÃO
 *
 *  Objetivo:
 *      - no protocolo devem estar definidas medidas para o comportamento da sincronização
 *      - os metodos de sincronização devem ter devidas regras estipuladas (tempo + processo)
 *      - o protocolo de sincronização faz recurso do protocolo de transferência de dados
 *      -
 *
 * */

public class FFSync {
    private static int PORT = 8888; // Port used to access, should be 80

    private File currentDirectory;
    private List<Node> nodes = new ArrayList<>(); // Connected nodes.

    private Lock lock = new ReentrantLock();

    // metadados dos ficheiros - ou estrutura equivalente


    public FFSync(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public static void main(String[] args) throws UnknownHostException {


        // Validating the arguments
        if (args.length < 1) System.exit(1); // Invalid number of arguments

        File directory = new File(args[0]);
        //if (!directory.exists()) System.exit(2); // Directory do not exist

        FFSync ffSync = new FFSync(directory);

        for(int i = 1; i < args.length; i++) ffSync.addNode(new Node(args[i]));

    }

}
/*

 $ FFSync pasta1 10.3.3.1
 FFSync <pastas a sincronizar> <endereço de IP do sistema a sincronizar>
 FFSync <folder> <peer>


 Dps de executar o comando:
 1. Validar os parâmetros
 2. Verificar se há condições de funcionamento (Se há acesso à pasta e à rede).
 3. Começar a atender pedidos em TCP e UDP em simultâneo, ambos na porta 80.
 Usar outra porta sem ser a 80 no inicio para testar. Por exemplo, porta 8888.
 */
