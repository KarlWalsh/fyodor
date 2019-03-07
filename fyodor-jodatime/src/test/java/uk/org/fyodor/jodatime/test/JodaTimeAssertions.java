package uk.org.fyodor.jodatime.test;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;

final class JodaTimeAssertions extends Assertions {

    static LocalDateAssert assertThat(final LocalDate actual) {
        return new LocalDateAssert(actual);
    }
}
