package encryption;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

import static common.debugger.Debugger.log;

public class Encryption {
    private static final int debuggerLevel = 2;

    private static final String ALG = "AES";
    private static final byte[] keyValue = {'C', 'c', '2', '0', '1', 'P', 'l', 'T', 'p', '3', '4', '5', '6', '7', '8', '9'};
    private static final SecretKeySpec KEY = new SecretKeySpec(keyValue, ALG);

    public static byte[] encrypt(byte[] data) {
        byte[] returnValue = new byte[0];
        log("Data: " + Arrays.toString(data));

        try {
            Cipher c = Cipher.getInstance(ALG);
            c.init(Cipher.ENCRYPT_MODE, KEY);
            returnValue = c.doFinal(data);
        } catch (Exception e) {
            System.out.println("Error - Encrypt - [" + e + "].");
        }

        log("Encrypted data: " + Arrays.toString(returnValue));
        return returnValue;
    }

    public static byte[] decrypt(byte[] data) {
        byte[] returnValue = new byte[0];
        log("Encrypted data: " + Arrays.toString(data));

        try {
            Cipher c = Cipher.getInstance(ALG);
            c.init(Cipher.DECRYPT_MODE, KEY);
            returnValue = c.doFinal(data);
        } catch (Exception e) {
            System.out.println("Error - Decrypt - [" + e + "].");
        }
        log("Data: " + Arrays.toString(returnValue));
        return returnValue;
    }
}
