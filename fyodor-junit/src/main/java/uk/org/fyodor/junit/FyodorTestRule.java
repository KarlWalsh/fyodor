package uk.org.fyodor.junit;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.Temporality;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.generators.time.TimekeeperConfigurer;

import java.time.Clock;
import java.time.ZonedDateTime;

import static org.junit.rules.RuleChain.outerRule;

public final class FyodorTestRule implements TestRule {

    private final RuleChain delegate;

    public FyodorTestRule() {
        this(() -> Timekeeper.current().zonedDateTime());
    }

    private FyodorTestRule(final Generator<ZonedDateTime> currentDateTimeAndZone) {
        this.delegate = outerRule(new SeedRule())
                .around(new TimekeeperRule(currentDateTimeAndZone));
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return delegate.apply(base, description);
    }

    public Temporality current() {
        return Timekeeper.current();
    }

    public static FyodorTestRule from(final Clock clock) {
        return new FyodorTestRule(() -> clock.instant().atZone(clock.getZone()));
    }

    public static FyodorTestRule from(final TimekeeperConfigurer timekeeperConfigurer) {
        return from(timekeeperConfigurer.asClock());
    }

    public static FyodorTestRule fyodorTestRule() {
        return new FyodorTestRule();
    }
}
