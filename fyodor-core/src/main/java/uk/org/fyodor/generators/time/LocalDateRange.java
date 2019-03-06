package uk.org.fyodor.generators.time;

import uk.org.fyodor.range.Range;

import java.time.DateTimeException;
import java.time.LocalDate;

import static uk.org.fyodor.generators.time.CurrentFyodorClock.current;

public final class LocalDateRange extends Range<LocalDate> {

    private static final LocalDate MINIMUM = LocalDate.MIN;
    private static final LocalDate MAXIMUM = LocalDate.MAX;

    private LocalDateRange(final LocalDate lowerBound, final LocalDate upperBound) {
        super(lowerBound, upperBound);
    }

    public static LocalDateRange tomorrow() {
        final LocalDate tomorrow = current().date().plusDays(1);
        return new LocalDateRange(tomorrow, tomorrow);
    }

    public static LocalDateRange all() {
        return new LocalDateRange(MINIMUM, MAXIMUM);
    }

    public static LocalDateRange today() {
        final LocalDate today = current().date();
        return new LocalDateRange(today, today);
    }

    public static LocalDateRange inTheFuture() {
        final LocalDate today = current().date();
        if (today.equals(MAXIMUM)) {
            throw new IllegalArgumentException("Date range cannot be in the future because today is the maximum date");
        }
        return new LocalDateRange(today.plusDays(1), MAXIMUM);
    }

    public static LocalDateRange inThePast() {
        final LocalDate today = current().date();
        if (today.equals(MINIMUM)) {
            throw new IllegalArgumentException("Date range cannot be in the past because today is the minimum date");
        }
        return new LocalDateRange(MINIMUM, today.minusDays(1));
    }

    public static LocalDateRange after(final LocalDate date) {
        if (date.equals(MAXIMUM)) {
            throw new IllegalArgumentException("Date range cannot be after the maximum date");
        }
        return new LocalDateRange(date.plusDays(1), MAXIMUM);
    }

    public static LocalDateRange before(final LocalDate date) {
        if (date.equals(MINIMUM)) {
            throw new IllegalArgumentException("Date range cannot be before the minimum date");
        }
        return new LocalDateRange(MINIMUM, date.minusDays(1));
    }

    public static LocalDateRange aged(final Range<ChronoAmount> range) {
        final LocalDate today = current().date();

        validateAgeRangeAgainst(range, today);

        final ChronoAmount lowerBound = range.lowerBound();
        final ChronoAmount upperBound = range.upperBound();

        final LocalDate latestDate = lowerBound.subtractFrom(today);
        final LocalDate earliestDate = upperBound.subtractFrom(today);

        return new LocalDateRange(earliestDate, latestDate);
    }

    private static void validateAgeRangeAgainst(final Range<? extends ChronoAmount> range, final LocalDate today) {
        try {
            range.lowerBound().subtractFrom(today);
        } catch (final DateTimeException dte) {
            throw new IllegalArgumentException("Date range cannot be earlier than the minimum date");
        }

        try {
            range.upperBound().subtractFrom(today);
        } catch (final DateTimeException dte) {
            throw new IllegalArgumentException("Date range cannot be earlier than the minimum date");
        }
    }
}
