package common.debugger;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Debugger {
    private static final String file = "log.txt";
    private static boolean toFile = false;
    private static boolean enable = true;
    private static int level = 4;

    private static OutputStream out;

    public static void initialize() {
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toFile = false;
            out = null;
        }
    }

    public static void close() {
        if (toFile) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused")
    public static void enable() {
        Debugger.enable = true;
    }

    @SuppressWarnings("unused")
    public static void disable() {
        Debugger.enable = false;
    }

    public static void setLevel(int level) {
        Debugger.level = level;
    }

    // Level 0
    public static void log(Object object) {
        PrintStream pw = System.out;
        if (enable) {
            if (toFile) pw = new PrintStream(out);
            pw.println("[" + DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.now().truncatedTo(ChronoUnit.SECONDS)) + "] "
                    + object.toString());
        }
    }

    // Custom level
    public static void log(Object object, int level) {
        if (Debugger.level >= level) log(object);
    }


    public static String toHexString(byte[] bytes) {
        StringBuilder stringBuffer =new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) stringBuffer.append('0');
            stringBuffer.append(hex);
        }
        return stringBuffer.toString();
    }
}
