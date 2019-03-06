package uk.org.fyodor.generators.time;

import java.time.*;

interface Temporality {
    LocalDate date();

    LocalTime time();

    LocalDateTime dateTime();

    Instant instant();

    Clock clock();

    ZonedDateTime zonedDateTime();

    ZoneId zone();
}
