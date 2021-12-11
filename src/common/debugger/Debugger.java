package common.debugger;

public class Debugger {
    private static boolean enable = true;
    private static int level = 1;
    private static final boolean toFile = false;
    private static final String file = "log.txt";

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
        if (enable) System.out.println("[Debug] " + object.toString());
    }

    // Custom level
    public static void log(Object object, int level) {
        if (Debugger.level >= level) log(object);
    }
}
