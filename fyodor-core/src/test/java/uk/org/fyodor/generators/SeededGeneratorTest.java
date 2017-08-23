package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.generators.time.InstantRange;
import uk.org.fyodor.generators.time.LocalDateRange;
import uk.org.fyodor.generators.time.LocalTimeRange;
import uk.org.fyodor.random.RandomSourceProvider;
import uk.org.fyodor.range.Range;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.generators.RDG.*;
import static uk.org.fyodor.generators.SeededGeneratorTest.NamedGenerator.generator;
import static uk.org.fyodor.generators.characters.CharacterSetFilter.LettersOnly;
import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;

public final class SeededGeneratorTest {

    @Test
    public void generatorsProduceTheSameValueForTheSameSeed() {
        generatorsToTest().forEach(SeededGeneratorTest::check);
    }

    private static void check(final NamedGenerator generator) {
        final long seed = longVal().next();

        RandomSourceProvider.seed().next(seed);
        final Object first = generator.next();

        RandomSourceProvider.seed().next(seed);
        final Object second = generator.next();

        assertThat(first)
                .describedAs(generator.name())
                .isEqualTo(second);
    }

    @SuppressWarnings("RedundantCast")
    private static List<NamedGenerator> generatorsToTest() {
        return new ArrayList<NamedGenerator>() {
            {
                add(generator("integer", integer(100)));
                add(generator("instant", instant()));
                add(generator("instant", instant(InstantRange.all())));
                add(generator("instant", instant((Range<Instant>) InstantRange.all())));
                add(generator("localTime", localTime()));
                add(generator("localTime", localTime(LocalTimeRange.all())));
                add(generator("localTime", localTime((Range<LocalTime>) LocalTimeRange.all())));
                add(generator("localDate", localDate()));
                add(generator("localDate", localDate(LocalDateRange.all())));
                add(generator("localDate", localDate((Range<LocalDate>) LocalDateRange.all())));
                add(generator("zonedDateTime", zonedDateTime()));
                add(generator("zonedDateTime", zonedDateTime(LocalDateRange.all(), LocalTimeRange.all())));
                add(generator("zonedDateTime", zonedDateTime((Range<LocalDate>) LocalDateRange.all(), (Range<LocalTime>) LocalTimeRange.all())));
                add(generator("localDateTime", localDateTime()));
                add(generator("localDateTime", localDateTime(LocalDateRange.all(), LocalTimeRange.all())));
                add(generator("localDateTime", localDateTime((Range<LocalDate>) LocalDateRange.all(), (Range<LocalTime>) LocalTimeRange.all())));
                add(generator("zoneId", zoneId()));
                add(generator("zoneOffset", zoneOffset()));
                add(generator("zoneOffset", zoneOffset(closed(-18, 18))));
                add(generator("bool", bool()));
                add(generator("byteVal", byteVal()));
                add(generator("byteVal", byteVal(closed(0, 50))));
                add(generator("byteArray", byteArray()));
                add(generator("shortVal", shortVal()));
                add(generator("shortVal", shortVal(closed(0, 50))));
                add(generator("integer", integer()));
                add(generator("integer", integer(100)));
                add(generator("integer", integer(closed(0, 100))));
                add(generator("longVal", longVal()));
                add(generator("longVal", longVal(100L)));
                add(generator("longVal", longVal(closed(0L, 100L))));
                add(generator("doubleVal", doubleVal()));
                add(generator("doubleVal", doubleVal(100d)));
                add(generator("doubleVal", doubleVal(closed(0d, 100d))));
                add(generator("bigDecimal", bigDecimal()));
                add(generator("bigDecimal", bigDecimal(100d)));
                add(generator("bigDecimal", bigDecimal(100L)));
                add(generator("bigDecimal", bigDecimal(BigDecimal.TEN)));
                add(generator("bigDecimal", bigDecimal(closed(BigDecimal.ZERO, BigDecimal.TEN))));
                add(generator("bigDecimal", bigDecimal(closed(BigDecimal.ZERO, BigDecimal.TEN), 2)));
                add(generator("uuid", uuid()));
                add(generator("string", string()));
                add(generator("string", string(10)));
                add(generator("string", string(10, "ABCD")));
                add(generator("string", string(fixed(10), "ABCD")));
                add(generator("string", string(10, LettersOnly)));
                add(generator("string", string(fixed(10), LettersOnly)));
                add(generator("string", string(10, LettersOnly)));
                add(generator("list", list(integer(10))));
                add(generator("array", array(Integer.class, integer(10))));
                add(generator("array", array(Integer.class, integer(10), 5)));
                add(generator("array", array(Integer.class, integer(10), closed(5, 10))));
                add(generator("set", RDG.set(integer())));
                add(generator("set", RDG.set(integer(), 3)));
                add(generator("set", RDG.set(integer(), fixed(4))));
                add(generator("map", map(integer(), string(10))));
                add(generator("map", map(integer(), string(10), 3)));
                add(generator("map", map(integer(), string(10), fixed(4))));
                add(generator("niNumber", niNumber()));
                add(generator("postcode", postcode()));
                add(generator("percentageChanceOf", percentageChanceOf(50)));
                add(generator("currency", currency()));
                add(generator("locale", locale()));
                add(generator("iso3Country", iso3Country()));
                add(generator("uri", uri()));
                add(generator("domainSuffix", domainSuffix()));
                add(generator("emailAddress", emailAddress()));
                add(generator("domain", domain()));
                add(generator("domain", domain(closed(10, 15))));
                add(generator("value", value(TestEnum.class)));
                add(generator("value", value(1, 2, 3, 4, 5, 6, 7, 8, 9)));
                add(generator("value", value(new int[] {9, 8, 7, 6, 5, 4, 3, 2, 1})));
                add(generator("value", value(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1))));
            }
        };
    }

    @SuppressWarnings("unused")
    enum TestEnum {
        A, B, C, D, E, F, G, H
    }

    static final class NamedGenerator<T> implements Generator<T> {

        private Generator<T> generator;
        private String name;

        @Override
        public T next() {
            return generator.next();
        }

        String name() {
            return name;
        }

        static <T> NamedGenerator<T> generator(final String name, final Generator<T> generator) {
            final NamedGenerator<T> namedGenerator = new NamedGenerator<>();
            namedGenerator.generator = generator;
            namedGenerator.name = name;
            return namedGenerator;
        }
    }
}
