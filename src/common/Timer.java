package common;

import java.time.Clock;

public class Timer {
    private final long start = Clock.systemDefaultZone().millis();

    public long getMilliseconds() {
        return Clock.systemDefaultZone().millis() - start;
    }
}
