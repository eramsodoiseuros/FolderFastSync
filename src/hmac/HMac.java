package hmac;

import encryption.Encryption;

import javax.crypto.Mac;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static common.debugger.Debugger.log;

public class HMac {
    private static final int debuggerLevel = 1;
    public static final int HeaderSize = 20; // For HmacSHA1
    private static final String HMAC = "HmacSHA1";
    private static final String keyValueHMAC = "G8T267ASNyjxbN/U2jFLgw==";

    public static byte[] calculateHMAC(byte[] data) {
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

    public static boolean confirmHMAC(byte[] data, byte[] hmac) {
        boolean returnValue = false;
        try {
            Mac mac = Mac.getInstance(HMAC);
            mac.init(Encryption.convertStringToSecretKey(keyValueHMAC));
            byte[] hmac2 = mac.doFinal(data);

            if (Arrays.equals(hmac2, hmac)) {
                returnValue = true;
            }
        } catch (Exception e) {
            System.out.println("Error - Confirmation on HMAC - [" + e + "].");
        }
        return returnValue;
    }

    public static byte[] addHeaderHMAC(byte[] data) {
        ByteBuffer bb = ByteBuffer.allocate(HeaderSize + data.length);
        bb.put(calculateHMAC(data));
        bb.put(data);

        return bb.array();
    }

    public static byte[] confirmPacketHeader(byte[] packet) throws PacketCorruptedException {
        ByteBuffer bb = ByteBuffer.wrap(packet);
        byte[] hmacHeader = new byte[HeaderSize];
        bb.get(hmacHeader, 0, HeaderSize);
        byte[] data = new byte[bb.remaining()];
        bb.get(data);

        if (!confirmHMAC(data, hmacHeader)) throw new PacketCorruptedException();
        log("HMac header checks...", debuggerLevel);
        return data;
    }
}
