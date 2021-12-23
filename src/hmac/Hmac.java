package hmac;

import encryption.Encryption;

import javax.crypto.Mac;
import java.util.Arrays;

public class Hmac {
    private static final String HMAC = "HmacSHA256";
    private static final String keyValueHMAC = "G8T267ASNyjxbN/U2jFLgw==";
    public static byte[] calculateHMAC(byte[] data){
        byte[] returnValue = new byte[0];
        try {
            Mac mac = Mac.getInstance(HMAC);
            mac.init(Encryption.convertStringToSecretKey(keyValueHMAC));
            returnValue = mac.doFinal(data);
        } catch (Exception e) {
            System.out.println("Error - HMAC - [" + e + "].");
        }
        return returnValue;
    }

    public static boolean confirmHMAC(byte[] data, byte[] hmac){
        boolean returnValue = false;
        try {
            Mac mac = Mac.getInstance(HMAC);
            mac.init(Encryption.convertStringToSecretKey(keyValueHMAC));
            byte[] hmac2 = mac.doFinal(data);

            if(Arrays.equals(hmac2, hmac)){
                returnValue = true;
            }
        } catch (Exception e) {
            System.out.println("Error - Confirmation on HMAC - [" + e + "].");
        }
        return returnValue;
    }
}
