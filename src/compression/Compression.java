package compression;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Compression {
    public static byte[] compress(byte[] dataToCompress) {
        byte[] returnValue = new byte[0];

        try {
            ByteArrayOutputStream byteStream =
                    new ByteArrayOutputStream(dataToCompress.length);
            try (byteStream) {
                try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
                    zipStream.write(dataToCompress);
                }
            }

            returnValue = byteStream.toByteArray();

        } catch (Exception e) {
            System.out.println("Error - Compress - [" + e + "].");
        }

        return returnValue;
    }

    public static byte[] decompress(byte[] data) {

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

        return os.toByteArray();
    }
}
