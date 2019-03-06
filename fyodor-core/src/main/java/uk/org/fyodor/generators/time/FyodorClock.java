package uk.org.fyodor.generators.time;

import java.time.*;

public interface FyodorClock {
    LocalDate date();

    LocalTime time();

    LocalDateTime dateTime();

    Instant instant();

    ZoneId zone();

    static FyodorClock fixed(final LocalDate localDate) {
        return fixed(localDate.atTime(12, 0).toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
    }

    static FyodorClock fixed(final LocalDateTime localDateTime, final ZoneId zone) {
        return from(Clock.fixed(localDateTime.toInstant(ZoneOffset.UTC), zone));
    }

    static FyodorClock fixed(final ZonedDateTime zonedDateTime) {
        return fixed(zonedDateTime.toInstant(), zonedDateTime.getZone());
    }

    static FyodorClock fixed(final Instant instant, final ZoneId zone) {
        return from(Clock.fixed(instant, zone));
    }

    static FyodorClock from(final Clock clock) {
        return new FyodorClock() {
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
                return clock.instant();
            }

            @Override
            public ZoneId zone() {
                return clock.getZone();
            }

            private ZonedDateTime zonedDateTime() {
                return instant().atZone(zone());
            }
        };
    }

    static FyodorClock systemDefault() {
        return from(Clock.systemDefaultZone());
    }
}
