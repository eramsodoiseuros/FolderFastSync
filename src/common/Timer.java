package common;

import java.time.Clock;

public class Timer {
    private static long start;

    public static void startTimer() {
        start = Clock.systemDefaultZone().millis();
    }

    public static long getMiliseconds() {
        return Clock.systemDefaultZone().millis() - start;
    }
}
