package common.debugger;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Debugger {
    private static final String file = "log.txt";
    private static boolean toFile = false;
    private static boolean enable = true;
    private static int level = 2;
    private static final Lock lock = new ReentrantLock();

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
        lock.lock();
        try {
            Debugger.enable = true;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unused")
    public static void disable() {
        lock.lock();
        try {
            Debugger.enable = false;
        } finally {
            lock.unlock();
        }
    }

    public static void setLevel(int level) {
        lock.lock();
        try {
            Debugger.level = level;
        } finally {
            lock.unlock();
        }
    }

    // Level 0
    public static void log(Object object) {
        lock.lock();
        try {
            PrintStream pw = System.out;
            if (enable) {
                if (toFile) pw = new PrintStream(out);
                pw.println("[" + DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.now().truncatedTo(ChronoUnit.SECONDS)) + "] "
                        + object.toString());
            }
        } finally {
            lock.unlock();
        }
    }

    // Custom level
    public static void log(Object object, int level) {
        if (Debugger.level >= level) log(object);
    }
}
