package ffrapid_protocol;

import app.FFSync;
import ffrapid_protocol.control_packets.Ack;
import ffrapid_protocol.control_packets.ControlPacket;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
 * 4     Acknowledgment (Ack)
 * 5     Error (ERROR)
 * <p>
 * Tratar os diferentes opcodes
 * Guardar os ficheiros em pastas
 * Encriptar os envios dos ficheiros com chaves
 * <p>
 * 4. app.FFSync consegue trocar mensagens protocolares simples com o seu parceiro remoto (qualquer mensagem)
 * 5. Definir quais as mensagens a trocar para obter a lista de ficheiros do sistema remoto (sintaxe, semântica e diagrama
 * temporal)
 * 8. FFsync implementa transferência de pelo menos um ficheiro num sentido. Validar.
 */

public class FTRapid {
    private static final String KEY = "A0x2345";
    private static final String MAC_SHA256_ALGORITHM = "HmacSHA256";


    private static byte[] getHMac(byte[] data) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(KEY.getBytes(), MAC_SHA256_ALGORITHM);
            Mac mac = Mac.getInstance(MAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            return mac.doFinal(data);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void send(ControlPacket packet, DatagramSocket socket, InetAddress address, int port) throws IOException {
        byte[] data = packet.serialize();
        byte[] hmac = new byte[0];
        hmac = getHMac(data);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(hmac);
        stream.write(data);
        byte[] segment = stream.toByteArray();

        DatagramPacket datagramPacket = new DatagramPacket(segment, segment.length, address, port);
        socket.send(datagramPacket);
    }

    public static ControlPacket receive(DatagramSocket socket) throws IOException {
        DatagramPacket datagramPacket = receiveDatagram(socket);

        return ControlPacket.deserialize(datagramPacket.getData());
    }

    public static DatagramPacket receiveDatagram(DatagramSocket socket) throws IOException {
        byte[] inBuffer = new byte[FFSync.getMTU()]; // creates a packet to receive the data from the client
        DatagramPacket packet = new DatagramPacket(inBuffer, inBuffer.length);
        socket.receive(packet);

        return packet;
    }

    public static void sendAck(DatagramSocket socket, InetAddress address, int port, long seqNumber) throws IOException {
        send(new Ack(seqNumber), socket, address, port);
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
