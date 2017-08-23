package uk.org.fyodor.generators.time;

import org.junit.Test;

import java.time.*;

import static java.time.ZoneOffset.ofHours;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.generators.time.TimekeeperConfigurer.*;

public final class TimekeeperConfigurerTest {

    @Test
    public void defaultsToInitialDateTimeAndOffset() {
        final LocalDate initialDate = LocalDate.of(2017, 8, 23);
        final LocalTime initialTime = LocalTime.of(16, 11, 34);
        final ZoneOffset initialOffset = ofHours(1);

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialOffset));

        final Instant expected = initialDate.atTime(initialTime).toInstant(initialOffset);

        assertThat(timekeeperConfigurer().asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withDateMaintainsInitialTimeAndZone() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneId initialZone = ZoneId.of("America/Los_Angeles");

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialZone));

        final LocalDate newDate = LocalDate.of(1999, 12, 31);
        final Instant expected = newDate.atTime(initialTime).atZone(initialZone).toInstant();

        assertThat(date(1999, 12, 31).asClock().instant()).isEqualTo(expected);
        assertThat(date(newDate).asClock().instant()).isEqualTo(expected);
        assertThat(date(() -> newDate).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withDateAndAtTimeMaintainsInitialZone() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneId initialZone = ZoneId.of("America/Chicago");

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialZone));

        final LocalDate newDate = LocalDate.of(1999, 12, 31);
        final LocalTime newTime = LocalTime.of(11, 59, 59);
        final Instant expected = newDate.atTime(newTime).atZone(initialZone).toInstant();

        assertThat(date(1999, 12, 31).atTime(11, 59, 59).asClock().instant()).isEqualTo(expected);
        assertThat(date(newDate).atTime(newTime).asClock().instant()).isEqualTo(expected);
        assertThat(date(() -> newDate).atTime(() -> newTime).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withTimeAndAtDateMaintainsInitialZone() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneId initialZone = ZoneId.of("Africa/Addis_Ababa");

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialZone));

        final LocalDate newDate = LocalDate.of(1999, 12, 31);
        final LocalTime newTime = LocalTime.of(11, 59, 59);
        final Instant expected = newDate.atTime(newTime).atZone(initialZone).toInstant();

        assertThat(time(11, 59, 59).atDate(1999, 12, 31).asClock().instant()).isEqualTo(expected);
        assertThat(time(newTime).atDate(newDate).asClock().instant()).isEqualTo(expected);
        assertThat(time(() -> newTime).atDate(() -> newDate).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withDateAndTimeMaintainsInitialOffset() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneOffset initialOffset = ofHours(18);

        Timekeeper.from(date(initialDate).atTime(initialTime).atOffset(initialOffset));

        final LocalDateTime newDateTime = LocalDate.of(1999, 12, 31)
                .atTime(LocalTime.of(11, 59, 59));
        final Instant expected = newDateTime.toInstant(initialOffset);

        assertThat(dateAndTime(1999, 12, 31, 11, 59, 59).asClock().instant()).isEqualTo(expected);
        assertThat(dateAndTime(newDateTime).asClock().instant()).isEqualTo(expected);
        assertThat(dateAndTime(() -> newDateTime).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withTimeMaintainsInitialDateAndOffset() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneOffset initialOffset = ofHours(18);

        Timekeeper.from(date(initialDate).atTime(initialTime).atOffset(initialOffset));

        final LocalTime newTime = LocalTime.of(11, 59, 59);
        final Instant expected = initialDate.atTime(newTime).toInstant(initialOffset);

        assertThat(time(11, 59, 59).asClock().instant()).isEqualTo(expected);
        assertThat(time(newTime).asClock().instant()).isEqualTo(expected);
        assertThat(time(() -> newTime).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withTimeAndAtOffsetMaintainsInitialDate() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneOffset initialOffset = ofHours(18);

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialOffset));

        final LocalTime newTime = LocalTime.of(11, 59, 59);
        final ZoneOffset newOffset = ofHours(10);
        final Instant expected = initialDate.atTime(newTime).toInstant(newOffset);

        assertThat(time(11, 59, 59).atZone(newOffset).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withTimeAndAtZoneMaintainsInitialDate() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneId initialZone = ZoneId.of("Africa/Cairo");

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialZone));

        final LocalTime newTime = LocalTime.of(11, 59, 59);
        final ZoneId newOffset = ZoneId.of("Australia/Darwin");
        final Instant expected = initialDate.atTime(newTime).atZone(newOffset).toInstant();

        assertThat(time(11, 59, 59).atZone(newOffset).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withOffsetMaintainsInitialDateAndTime() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneOffset initialOffset = ofHours(18);

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialOffset));

        final ZoneOffset newOffset = ofHours(10);
        final Instant expected = initialDate.atTime(initialTime).toInstant(newOffset);

        assertThat(zone(newOffset).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withZoneMaintainsInitialDateAndTime() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneId initialZone = ZoneId.of("Africa/Addis_Ababa");

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialZone));

        final ZoneId newZone = ZoneId.of("Africa/Cairo");
        final Instant expected = initialDate.atTime(initialTime).atZone(newZone).toInstant();

        assertThat(zone(newZone).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withOffsetAtDateMaintainsInitialTime() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneOffset initialOffset = ofHours(18);

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialOffset));

        final ZoneOffset newOffset = ofHours(10);
        final LocalDate newDate = LocalDate.of(1999, 12, 31);
        final Instant expected = newDate.atTime(initialTime).toInstant(newOffset);

        assertThat(offset(newOffset).atDate(newDate).asClock().instant()).isEqualTo(expected);
        assertThat(offset(() -> newOffset).atDate(1999, 12, 31).asClock().instant()).isEqualTo(expected);
        assertThat(offset(() -> newOffset).atDate(() -> newDate).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withOffsetAtTimeMaintainsInitialDate() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneOffset initialOffset = ofHours(18);

        Timekeeper.from(date(initialDate).atTime(initialTime).atOffset(initialOffset));

        final ZoneOffset newOffset = ofHours(10);
        final LocalTime newTime = LocalTime.of(23, 59, 59);
        final Instant expected = initialDate.atTime(newTime).toInstant(newOffset);

        assertThat(offset(newOffset).atTime(newTime).asClock().instant()).isEqualTo(expected);
        assertThat(offset(newOffset).atTime(23, 59, 59).asClock().instant()).isEqualTo(expected);
        assertThat(offset(() -> newOffset).atTime(() -> newTime).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withOffsetAtDateAndAtTimeMaintainsInitialDate() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneOffset initialOffset = ofHours(18);

        Timekeeper.from(date(initialDate).atTime(initialTime).atOffset(initialOffset));

        final ZoneOffset newOffset = ofHours(10);
        final LocalDate newDate = LocalDate.of(1999, 12, 31);
        final LocalTime newTime = LocalTime.of(23, 59, 59);
        final Instant expected = newDate.atTime(newTime).toInstant(newOffset);

        assertThat(offset(newOffset).atDate(newDate).atTime(newTime).asClock().instant()).isEqualTo(expected);
        assertThat(offset(newOffset).atDate(1999, 12, 31).atTime(23, 59, 59).asClock().instant()).isEqualTo(expected);
        assertThat(offset(() -> newOffset).atDate(() -> newDate).atTime(() -> newTime).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void withOffsetAtDateTimeMaintainsInitialDate() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(0, 0, 1);
        final ZoneOffset initialOffset = ofHours(18);

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialOffset));

        final ZoneOffset newOffset = ofHours(10);
        final LocalDateTime newDateTime = LocalDateTime.of(1999, 12, 31, 23, 59, 59);
        final Instant expected = newDateTime.toInstant(newOffset);

        assertThat(offset(newOffset).atDateAndTime(newDateTime).asClock().instant()).isEqualTo(expected);
        assertThat(offset(newOffset).atDateAndTime(1999, 12, 31, 23, 59, 59).atTime(23, 59, 59).asClock().instant()).isEqualTo(expected);
        assertThat(offset(() -> newOffset).atDateAndTime(() -> newDateTime).asClock().instant()).isEqualTo(expected);
    }

    @Test
    public void newOffsetIsAppliedToExistingTimeWithZeroOffset() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(12, 0, 0);
        final ZoneOffset initialOffset = ofHours(0);

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialOffset));

        assertThat(offset(ofHours(2)).asClock().instant().toString()).isEqualTo("2000-01-01T10:00:00Z");
        assertThat(timekeeperConfigurer().atZone(ofHours(2)).asClock().instant().toString()).isEqualTo("2000-01-01T10:00:00Z");
    }

    @Test
    public void newOffsetIsAppliedToExistingTimeWithNonZeroOffset() {
        final LocalDate initialDate = LocalDate.of(2000, 1, 1);
        final LocalTime initialTime = LocalTime.of(12, 0, 0);
        final ZoneOffset initialOffset = ofHours(18);

        Timekeeper.from(date(initialDate).atTime(initialTime).atZone(initialOffset));

        assertThat(offset(ofHours(2)).asClock().instant().toString()).isEqualTo("2000-01-01T10:00:00Z");
        assertThat(timekeeperConfigurer().atZone(ofHours(2)).asClock().instant().toString()).isEqualTo("2000-01-01T10:00:00Z");
    }
}