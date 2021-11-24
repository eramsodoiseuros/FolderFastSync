import java.security.NoSuchAlgorithmException;

/**
 *
 * PROTOCOLO DE TRANSFERÊNCIA DE DADOS
 *
 * Objetivo:
 *      - formular um cabeçalho próprio
 *      - realizar transferência de ficheiros
 *      - https://datatracker.ietf.org/doc/html/rfc1350/   -tftp
 *      - https://www.geeksforgeeks.org/stop-and-wait-arq/
 *      - https://www.geeksforgeeks.org/sliding-window-protocol-set-1/
 *      - https://www.geeksforgeeks.org/sliding-window-protocol-set-2-receiver-side/
 *      - https://www.geeksforgeeks.org/sliding-window-protocol-set-3-selective-repeat/
 * */

public class FTRapid {
    public static void main(String[] args) {
        int maxKeySize = 0;
        try {
            maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("erro [" + e.getMessage() + "]");
        }
        System.out.println("Max Key Size for AES : " + maxKeySize);
    }
}
