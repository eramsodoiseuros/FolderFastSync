package common.debugger;

public class Debugger {
    private static boolean enable = true;

    @SuppressWarnings("unused")
    public static void enable() {
        Debugger.enable = true;
    }

    @SuppressWarnings("unused")
    public static void disable() {
        Debugger.enable = false;
    }

    public static void log(Object object) {
        if (enable) System.out.println("[Debug] " + object.toString());
    }
}
