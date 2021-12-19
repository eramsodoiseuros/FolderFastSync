package test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HMac {

    private static String toHexString(byte[] bytes){
        StringBuilder hash = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) hash.append('0');
            hash.append(hex);
        }
        return hash.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        final String KEY = "12345";
        final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        final String msg = "HMac1";

        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);

        mac.init(secretKeySpec);
        byte[] encryptedMsg = mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
        System.out.println("Message: " + msg);
        System.out.println("Encrypted message: " + toHexString(encryptedMsg));
        System.out.println(HMAC_SHA1_ALGORITHM + " length: " + encryptedMsg.length);
    }
}
