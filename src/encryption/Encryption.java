package encryption;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

import static common.debugger.Debugger.log;

public class Encryption {
    private static final int debuggerLevel = 2;

    private static final String ALG = "AES";
    private static final String keyValue = "jG7zvqh/HYkj0jUVCTqSQA==";

    public static SecretKey convertStringToSecretKeyto(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static byte[] encrypt(byte[] data) {
        byte[] returnValue = new byte[0];
        log("Encrypt | Data: " + Arrays.toString(data), debuggerLevel);

        try {
            Cipher c = Cipher.getInstance(ALG);
            c.init(Cipher.ENCRYPT_MODE, convertStringToSecretKeyto(keyValue));
            returnValue = c.doFinal(data);
        } catch (Exception e) {
            System.out.println("Error - Encrypt - [" + e + "].");
        }

        log("Encrypt | Encrypted data: " + Arrays.toString(returnValue), debuggerLevel);
        return returnValue;
    }

    public static byte[] decrypt(byte[] data) {
        byte[] returnValue = new byte[0];
        log("Decrypt | Encrypted data: " + Arrays.toString(data), debuggerLevel);

        try {
            Cipher c = Cipher.getInstance(ALG);
            c.init(Cipher.DECRYPT_MODE, convertStringToSecretKeyto(keyValue));
            returnValue = c.doFinal(data);
        } catch (Exception e) {
            System.out.println("Error - Decrypt - [" + e + "].");
        }
        log("Decrypt | Data: " + Arrays.toString(returnValue), debuggerLevel);
        return returnValue;
    }
}
