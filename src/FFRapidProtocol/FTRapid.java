package FFRapidProtocol;

import FFRapidProtocol.ControlPacketTypes.ACK;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
 * <p>
 * <p>
 * ????????? ver isto com o stor
 * <p>
 *  TODO:
 *      DEFINIR OS opcodes
 * <p>
 * opcode  operation
 * 1     Read request (RRQ)
 * 2     Write request (WRQ)
 * 3     Data (DATA)
 * 4     Acknowledgment (ACK)
 * 5     Error (ERROR)
 * <p>
 * Tratar os diferentes opcodes
 * Guardar os ficheiros em pastas
 * Encriptar os envios dos ficheiros com chaves
 * <p>
 * 4. FFSync consegue trocar mensagens protocolares simples com o seu parceiro remoto (qualquer mensagem)
 * 5. Definir quais as mensagens a trocar para obter a lista de ficheiros do sistema remoto (sintaxe, semântica e diagrama
 * temporal)
 * 8. FFsync implementa transferência de pelo menos um ficheiro num sentido. Validar.
 */

public class FTRapid {

    public void send(DatagramSocket socket, byte[] d) {
        try {
            ControlPacket m = new ACK(10);
            byte[] buf = m.toBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receive(DatagramSocket socket) {
        byte[] inBuffer = new byte[1492]; // MTU - UDPLength
        DatagramPacket packet = new DatagramPacket(inBuffer, inBuffer.length);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
            inBuffer = null;
        }
        return inBuffer;
    }

    public static void main(String[] args) {
        int maxKeySize = 0;
        try {
            maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("erro FFRapidProtocol.FTRapid [" + e.getMessage() + "]");
        }
        System.out.println("Max Key Size for AES : " + maxKeySize);
    }
}
