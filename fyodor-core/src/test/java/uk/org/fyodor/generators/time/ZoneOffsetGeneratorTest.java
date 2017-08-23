package uk.org.fyodor.generators.time;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.org.fyodor.Sampler;
import uk.org.fyodor.Sampler.Sample;

import java.time.ZoneOffset;
import java.util.List;

import static java.time.ZoneOffset.ofHours;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static uk.org.fyodor.generators.RDG.zoneOffset;
import static uk.org.fyodor.range.Range.closed;

public final class ZoneOffsetGeneratorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void generatesNonNullZoneOffsetsWithinZoneOffsetRange() {
        final Sample<ZoneOffset> sample = Sampler.from(zoneOffset()).sample(1000);

        final List<ZoneOffset> expectedHourOffsets = rangeClosed(-18, 18)
                .mapToObj(ZoneOffset::ofHours)
                .collect(toList());

        assertThat(sample.unique())
                .containsAll(expectedHourOffsets);
    }

    @Test
    public void generatesNonNullZoneOffsetsWithinRange() {
        final Sample<ZoneOffset> sample = Sampler.from(zoneOffset(closed(-5, 5))).sample(1000);

        final List<ZoneOffset> expectedHourOffsets = rangeClosed(-5, 5)
                .mapToObj(ZoneOffset::ofHours)
                .collect(toList());

        assertThat(sample.unique())
                .containsAll(expectedHourOffsets)
                .doesNotContain(ofHours(-6))
                .doesNotContain(ofHours(6));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void offsetRangeMustBeWithinMaxUpperBound() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Upper bound 19 must be within the range -18..18"));

        zoneOffset(closed(-18, 19));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void offsetRangeMustBeWithinMinLowerBound() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Lower bound -19 must be within the range -18..18"));

        zoneOffset(closed(-19, 18));
    }
}
