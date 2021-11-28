import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/**
 * PROTOCOLO DE TRANSFERÊNCIA DE DADOS
 * <p>
 * Objetivo:
 * - formular um cabeçalho próprio
 * - realizar transferência de ficheiros
 * - https://datatracker.ietf.org/doc/html/rfc1350/   -tftp
 * - https://www.geeksforgeeks.org/stop-and-wait-arq/
 * - https://www.geeksforgeeks.org/sliding-window-protocol-set-1/
 * - https://www.geeksforgeeks.org/sliding-window-protocol-set-2-receiver-side/
 * - https://www.geeksforgeeks.org/sliding-window-protocol-set-3-selective-repeat/
 *
 *
 *  ????????? ver isto com o stor
 *
 *  TODO:
 *      DEFINIR OS opcodes
 *
     *      opcode  operation
     *             1     Read request (RRQ)
     *             2     Write request (WRQ)
     *             3     Data (DATA)
     *             4     Acknowledgment (ACK)
     *             5     Error (ERROR)
 *
 *      Tratar os diferentes opcodes
 *      Guardar os ficheiros em pastas
 *      Encriptar os envios dos ficheiros com chaves
 *
 *      4. FFSync consegue trocar mensagens protocolares simples com o seu parceiro remoto (qualquer mensagem)
 *      5. Definir quais as mensagens a trocar para obter a lista de ficheiros do sistema remoto (sintaxe, semântica e diagrama
 * temporal)
 *      8. FFsync implementa transferência de pelo menos um ficheiro num sentido. Validar.
 */

public class FTRapid{
    private final Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public static class Frame{
        public final int tag;
        public final byte[] data;
        public Frame(int tag, byte[] data){
            this.tag = tag;
            this.data = data;
        }
    }

    public FTRapid(Socket s) throws IOException {
        socket = s;
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(int tag, byte[] d) {
        //
        try {
            out.writeInt(d.length);  // envia tamanho
            out.writeInt(tag);
            out.write(d);            // envia conteudo
            out.flush();
        } catch (IOException e) {
            System.out.println("erro FTRapid - Send [" + e.getMessage() + "]");
        }
    }

    public Frame receive(){
        Frame frame = null;
        byte[] data;
        int tag;
        
        try {
            data = new byte[in.readInt()];
            tag = in.readInt();
            in.readFully(data);
            frame = new Frame(tag,data);
        } catch (IOException e) {
            System.out.println("erro FTRapid - Receive [" + e.getMessage() + "]");
        }

        return frame;
    }

    public static void main(String[] args) {
        int maxKeySize = 0;
        try {
            maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("erro FTRapid [" + e.getMessage() + "]");
        }
        System.out.println("Max Key Size for AES : " + maxKeySize);
    }
}
