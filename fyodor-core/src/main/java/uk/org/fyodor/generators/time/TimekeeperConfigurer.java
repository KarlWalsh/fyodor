package uk.org.fyodor.generators.time;

import uk.org.fyodor.generators.Generator;

import java.time.*;

@SuppressWarnings("WeakerAccess")
public final class TimekeeperConfigurer {

    private Generator<LocalDate> date = () -> Timekeeper.current().date();
    private Generator<LocalTime> time = () -> Timekeeper.current().time();
    private Generator<? extends ZoneId> zone = () -> Timekeeper.current().zone();

    public TimekeeperConfigurer atDateAndTime(final int year, final int month, final int dayOfMonth,
                                              final int hour, final int minute, final int second) {
        return atDateAndTime(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second));
    }

    public TimekeeperConfigurer atDateAndTime(final LocalDateTime dateTime) {
        return atDateAndTime(() -> dateTime);
    }

    public TimekeeperConfigurer atDateAndTime(final Generator<LocalDateTime> dateTime) {
        return atDate(() -> dateTime.next().toLocalDate())
                .atTime(() -> dateTime.next().toLocalTime());
    }

    public TimekeeperConfigurer atDate(final int year, final int month, final int dayOfMonth) {
        return atDate(LocalDate.of(year, month, dayOfMonth));
    }

    public TimekeeperConfigurer atDate(final LocalDate date) {
        return atDate(() -> date);
    }

    public TimekeeperConfigurer atDate(final Generator<LocalDate> date) {
        this.date = date;
        return this;
    }

    public TimekeeperConfigurer atTime(final int hour, final int minute, final int second) {
        return atTime(LocalTime.of(hour, minute, second));
    }

    public TimekeeperConfigurer atTime(final LocalTime time) {
        return atTime(() -> time);
    }

    public TimekeeperConfigurer atTime(final Generator<LocalTime> time) {
        this.time = time;
        return this;
    }

    public TimekeeperConfigurer atZone(final ZoneId zoneId) {
        return atZone(() -> zoneId);
    }

    public TimekeeperConfigurer atZone(final Generator<? extends ZoneId> zoneId) {
        this.zone = zoneId;
        return this;
    }

    public TimekeeperConfigurer atOffset(final ZoneOffset offset) {
        return atZone(offset);
    }

    public TimekeeperConfigurer atOffset(final Generator<ZoneOffset> offset) {
        return atZone(offset);
    }

    public Clock asClock() {
        final ZoneId zone = this.zone.next();
        final LocalDate date = this.date.next();
        final LocalTime time = this.time.next();
        return Clock.fixed(ZonedDateTime.of(date, time, zone).toInstant(), zone);
    }

    public static TimekeeperConfigurer offset(final ZoneOffset offset) {
        return offset(() -> offset);
    }

    public static TimekeeperConfigurer offset(final Generator<ZoneOffset> offset) {
        return timekeeperConfigurer().atOffset(offset);
    }

    public static TimekeeperConfigurer zone(final ZoneId zone) {
        return zone(() -> zone);
    }

    public static TimekeeperConfigurer zone(final Generator<ZoneId> zone) {
        return timekeeperConfigurer().atZone(zone);
    }

    public static TimekeeperConfigurer timekeeperConfigurer() {
        return new TimekeeperConfigurer();
    }

    public static TimekeeperConfigurer time(final int hour, final int minute, final int second) {
        return time(LocalTime.of(hour, minute, second));
    }

    public static TimekeeperConfigurer time(final LocalTime time) {
        return timekeeperConfigurer().atTime(time);
    }

    public static TimekeeperConfigurer time(final Generator<LocalTime> time) {
        return timekeeperConfigurer().atTime(time);
    }

    public static TimekeeperConfigurer date(final int year, final int month, final int dayOfMonth) {
        return date(LocalDate.of(year, month, dayOfMonth));
    }

    public static TimekeeperConfigurer date(final LocalDate date) {
        return timekeeperConfigurer().atDate(date);
    }

    public static TimekeeperConfigurer date(final Generator<LocalDate> date) {
        return timekeeperConfigurer().atDate(date);
    }

    public static TimekeeperConfigurer dateAndTime(final int year, final int month, final int dayOfMonth,
                                                   final int hour, final int minute, final int second) {
        return dateAndTime(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second));
    }

    public static TimekeeperConfigurer dateAndTime(final LocalDateTime dateTime) {
        return timekeeperConfigurer().atDateAndTime(dateTime);
    }

    public static TimekeeperConfigurer dateAndTime(final Generator<LocalDateTime> dateTime) {
        return timekeeperConfigurer().atDateAndTime(dateTime);
    }
}
