package compression;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static common.debugger.Debugger.log;

public class Compression {
    private static final int debuggerLevel = 2;

    public static byte[] compress(byte[] dataToCompress) {
        byte[] returnValue = new byte[0];
        log("Compress | Data: " + Arrays.toString(dataToCompress), debuggerLevel);

        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(dataToCompress.length);
            try (byteStream) {
                try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
                    zipStream.write(dataToCompress);
                }
            }

            returnValue = byteStream.toByteArray();

        } catch (Exception e) {
            System.out.println("Error - Compress - [" + e + "].");
        }

        log("Compress | Compressed data: " + Arrays.toString(returnValue), debuggerLevel);
        return returnValue;
    }

    public static byte[] decompress(byte[] data) {
        log("Decompress | Compressed data: " + Arrays.toString(data), debuggerLevel);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            GZIPInputStream gis = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.close();
            gis.close();
        } catch (Exception e) {
            System.out.println("Error - Decompress - [" + e + "].");
            return null;
        }

        log("Decompress | Compressed data: " + Arrays.toString(os.toByteArray()), debuggerLevel);
        return os.toByteArray();
    }
}
