package uk.org.fyodor.generators.time;

import static java.lang.ThreadLocal.withInitial;

public final class CurrentFyodorClock {

    private static final ThreadLocal<FyodorClock> clocks = withInitial(FyodorClock::systemDefault);

    private CurrentFyodorClock() {
    }

    public static void set(final FyodorClock clock) {
        clocks.set(clock);
    }

    public static FyodorClock current() {
        return clocks.get();
    }

}
