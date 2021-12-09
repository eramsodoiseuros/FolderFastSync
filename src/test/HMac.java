package test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HMac {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        final String KEY = "12345";
        final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        final String msg = "HMac";

        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(secretKeySpec);
        System.out.println(Arrays.toString(mac.doFinal(msg.getBytes(StandardCharsets.UTF_8))));
    }
}
