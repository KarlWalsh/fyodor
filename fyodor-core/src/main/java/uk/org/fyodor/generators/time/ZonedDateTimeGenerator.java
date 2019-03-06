package uk.org.fyodor.generators.time;

import uk.org.fyodor.generators.Generator;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static uk.org.fyodor.generators.time.CurrentFyodorClock.current;

public final class ZonedDateTimeGenerator implements Generator<ZonedDateTime> {

    private final Generator<LocalDateTime> localDateTimeGenerator;

    public ZonedDateTimeGenerator(final Generator<LocalDateTime> localDateTimeGenerator) {
        this.localDateTimeGenerator = localDateTimeGenerator;
    }

    @Override
    public ZonedDateTime next() {
        return localDateTimeGenerator.next().atZone(current().zone());
    }
}
