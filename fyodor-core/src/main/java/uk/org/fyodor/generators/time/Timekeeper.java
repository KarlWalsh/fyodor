package uk.org.fyodor.generators.time;

import java.time.*;

import static java.lang.ThreadLocal.withInitial;
import static java.time.Clock.systemDefaultZone;

public final class Timekeeper {

    private static final ThreadLocal<Temporality> temporalities = withInitial(() -> temporalityFrom(systemDefaultZone()));

    private Timekeeper() {
    }

    public static void from(final Clock clock) {
        temporalities.set(temporalityFrom(clock));
    }

    public static Temporality current() {
        return temporalities.get();
    }

    private static Temporality temporalityFrom(final Clock clock) {
        return new Temporality() {
            @Override
            public LocalDate date() {
                return zonedDateTime().toLocalDate();
            }

            @Override
            public LocalTime time() {
                return zonedDateTime().toLocalTime();
            }

            @Override
            public LocalDateTime dateTime() {
                return zonedDateTime().toLocalDateTime();
            }

            @Override
            public Instant instant() {
                return clock().instant();
            }

            @Override
            public Clock clock() {
                return clock;
            }

            @Override
            public ZonedDateTime zonedDateTime() {
                return instant().atZone(zone());
            }

            @Override
            public ZoneId zone() {
                return clock().getZone();
            }
        };
    }
}
