package uk.org.fyodor.junit.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import uk.org.fyodor.junit.ExtendWithFyodor;
import uk.org.fyodor.junit.test.TestRunner.TestRunReport;
import uk.org.fyodor.testapi.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.org.fyodor.generators.RDG.longVal;
import static uk.org.fyodor.junit.test.TestRunner.runnerFor;
import static uk.org.fyodor.random.RandomSourceProvider.seed;

final class SeedTest {

    private final long initialSeed = longVal().next();

    @BeforeEach
    void setInitialSeed() {
        seed().next(initialSeed);
    }

    @AfterEach
    void revertSeed() {
        seed().previous();
    }

    @Test
    void seedHasNotChangedAfterTest() {
        final long initialSeed = 4324534L;
        seed().next(initialSeed);

        runnerFor(SeededTest.class).run();

        assertThat(seed().current()).isEqualTo(initialSeed);
    }

    @Test
    void seedHasNotChangedWhenTestFailsToStart() {
        final long initialSeed = 4324534L;
        seed().next(initialSeed);

        runnerFor(BadConfiguration.class).run();

        assertThat(seed().current()).isEqualTo(initialSeed);
    }

    @Test
    void initialSeedIsReportedWhenTestFails() {
        final TestRunReport report = runnerFor(FailingTest.class).run();

        assertThat(report.detailsFor(FailingTest.class, "noSeedAnnotation").failure().getException())
                .isInstanceOf(FailedWithSeed.class)
                .hasMessage("Test failed with seed " + initialSeed)
                .hasCause(new AssertionFailedError("uh oh"));

        assertThat(report.detailsFor(FailingTest.class, "withSeedAnnotation").failure().getException())
                .isInstanceOf(FailedWithSeed.class)
                .hasMessage("Test failed with seed 1");
    }

    @ExtendWithFyodor
    static final class BadConfiguration {
        @Test
        void test() {
        }
    }

    @ExtendWithFyodor
    static final class FailingTest {
        @Test
        void noSeedAnnotation() {
            fail("uh oh");
        }

        @Test
        @Seed(1L)
        void withSeedAnnotation() {
            throw new IllegalArgumentException("this is an exception causing the test to fail");
        }
    }

    @ExtendWithFyodor
    static final class SeededTest {
        @Test
        @Seed(2837645L)
        void test() {
        }
    }

}
