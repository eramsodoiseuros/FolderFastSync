package common.debugger;

public class Debugger {
    private static boolean on = true;

    public static void setOn(boolean on) {
        Debugger.on = on;
    }

    public static void log(Object object) {
        if (on) System.out.println("[Debug] " + object.toString());
    }
}
