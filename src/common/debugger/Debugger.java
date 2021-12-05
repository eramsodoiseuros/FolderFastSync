package common.debugger;

public class Debugger {
    private static boolean on = true;

    public static void setOn(boolean on) {
        Debugger.on = on;
    }

    public static void print(String message) {
        if (on) System.out.println(message);
    }
}
