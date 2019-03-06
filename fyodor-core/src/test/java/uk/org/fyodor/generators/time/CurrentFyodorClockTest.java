package uk.org.fyodor.generators.time;

import org.junit.Test;

import java.time.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.generators.RDG.localDate;
import static uk.org.fyodor.generators.time.FyodorClock.fixed;
import static uk.org.fyodor.generators.time.FyodorClock.systemDefault;
import static uk.org.fyodor.generators.time.LocalDateRange.today;
import static uk.org.fyodor.generators.time.CurrentFyodorClock.current;

public class CurrentFyodorClockTest {

    @Test
    public void dateTimeIsAtClockZone() {
        final LocalDateTime bstDateTime = LocalDateTime.of(2017, 4, 3, 23, 59);
        final ZoneId zone = ZoneId.of("Australia/Sydney");
        final ZoneOffset offset = zone.getRules().getOffset(bstDateTime);

        CurrentFyodorClock.set(fixed(bstDateTime, zone));

        final LocalDateTime actual = current().dateTime();
        assertThat(actual).isEqualTo(bstDateTime.plusSeconds(offset.getTotalSeconds()));
    }

    @Test
    public void dateIsAtClockZone() {
        final LocalDateTime bstDateTime = LocalDateTime.of(2017, 1, 1, 23, 59);
        final ZoneId zone = ZoneId.of("Asia/Tokyo");
        final ZoneOffset offset = zone.getRules().getOffset(bstDateTime);

        CurrentFyodorClock.set(fixed(bstDateTime, zone));

        final LocalDate actual = current().date();
        assertThat(actual).isEqualTo(bstDateTime.plusSeconds(offset.getTotalSeconds()).toLocalDate());
    }

    @Test
    public void timeIsAtClockZone() {
        final LocalDateTime bstDateTime = LocalDateTime.of(2017, 6, 15, 23, 59);
        final ZoneId zone = ZoneId.of("Pacific/Apia");
        final ZoneOffset offset = zone.getRules().getOffset(bstDateTime);

        CurrentFyodorClock.set(fixed(bstDateTime, zone));

        final LocalTime actual = current().time();
        assertThat(actual).isEqualTo(bstDateTime.plusSeconds(offset.getTotalSeconds()).toLocalTime());
    }

    @Test
    public void currentDateCanBeConfiguredFromSystemClock() {
        CurrentFyodorClock.set(systemDefault());

        final LocalDate systemDate = LocalDate.now();

        final LocalDate today = current().date();

        assertThat(today).isEqualTo(systemDate);
    }

    @Test
    public void currentDateCanBeConfiguredByCustomClock() {
        final LocalDate systemDate = LocalDate.now();
        final Instant systemDateAsInstant = systemDate.atStartOfDay().toInstant(ZoneOffset.ofHours(0));

        CurrentFyodorClock.set(fixed(systemDateAsInstant, ZoneId.systemDefault()));

        final LocalDate today = current().date();

        assertThat(today).isEqualTo(systemDate);
    }

    @Test
    public void currentDateIsSpecificToTheCurrentThread() throws InterruptedException {
        final LocalDate firstDate = LocalDate.now().minusDays(1);
        final LocalDate secondDate = LocalDate.now();
        final LocalDate thirdDate = LocalDate.now().plusDays(1);

        final ResultHolder firstResult = new ResultHolder();
        final ResultHolder secondResult = new ResultHolder();
        final ResultHolder thirdResult = new ResultHolder();

        final ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executorService.execute(setAndThenGetCurrentDate(thirdDate, thirdResult));
            executorService.execute(setAndThenGetCurrentDate(firstDate, firstResult));
            executorService.execute(setAndThenGetCurrentDate(secondDate, secondResult));
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        assertThat(firstResult.uniqueValues()).containsExactly(firstDate);
        assertThat(secondResult.uniqueValues()).containsExactly(secondDate);
        assertThat(thirdResult.uniqueValues()).containsExactly(thirdDate);
    }

    private static Runnable setAndThenGetCurrentDate(final LocalDate fixedDate, final ResultHolder resultHolder) {
        return () -> {
            CurrentFyodorClock.set(fixed(fixedDate));
            final LocalDate today = localDate(today()).next();
            resultHolder.addResult(today);
        };
    }

    static final class ResultHolder {
        private List<LocalDate> values = new CopyOnWriteArrayList<>();

        void addResult(final LocalDate result) {
            values.add(result);
        }

        Set<LocalDate> uniqueValues() {
            return new HashSet<>(values);
        }
    }
}