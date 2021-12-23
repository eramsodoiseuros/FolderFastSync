package common.debugger;

import java.io.*;
import java.time.LocalDateTime;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

import static java.time.format.FormatStyle.MEDIUM;
import static java.time.temporal.ChronoUnit.SECONDS;

public class Debugger {

    private static final String file = "LOG" + timeNow() +  ".txt";

    private static boolean toFile = true;
    private static boolean enable = true;

    private static int level = 3;

    private static PrintStream stream_file;
    private static PrintStream stream_sout;

    public static void initialize() {
        try {
            stream_sout = System.out;
            if(toFile){
                stream_file = new PrintStream(new FileOutputStream(file));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error - LOG FILE NOT FOUND - [" + e + "]");
            toFile = false;
            stream_file.close();
        }
    }

    public static void close() {
        stream_sout.close();

        if (toFile) {
            stream_file.close();
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
        if (enable) {
            if(toFile){
                stream_file.println("[" + getTimeNow() + "] "
                        + object.toString());
            }

            stream_sout.println("[" + getTimeNow() + "] "
                    + object.toString());
        }
    }

    // Custom level
    public static void log(Object object, int level) {
        if (Debugger.level >= level) log(object);
    }

    private static String timeNow(){
        return DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now().truncatedTo(SECONDS)).replace(":","_");
    }

    private static String getTimeNow() {
        return DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.now().truncatedTo(SECONDS));
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) stringBuffer.append('0');
            stringBuffer.append(hex);
        }
        return stringBuffer.toString();
    }
}
