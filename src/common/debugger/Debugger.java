package common.debugger;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Debugger {
    private static final String file = "log" + getTimeNow() +  ".txt";
    private static boolean toFile = true;
    private static boolean enable = true;
    private static int level = 1;

    private static PrintStream stream;

    public static void initialize() {
        try {
            if (toFile) stream = new PrintStream(new FileOutputStream(file));
            else stream = System.out;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toFile = false;
            stream = System.out;
        }
    }

    public static void close() {
        if (toFile) {
            stream.close();
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
            stream.println("[" + getTimeNow() + "] "
                    + object.toString());
        }
    }

    // Custom level
    public static void log(Object object, int level) {
        if (Debugger.level >= level) log(object);
    }


    private static String getTimeNow() {
        return DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
    }
}
