package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.testapi.FailedWithSeed;
import uk.org.fyodor.testapi.Seed;

import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static uk.org.fyodor.junit.FyodorTestRule.fyodorTestRule;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.random.RandomSourceProvider.seed;

@SuppressWarnings("ConstantConditions")
public final class CurrentSeedTest {

    private static final Reporter<Long> reporter = reporter();

    private final TestRunner<Long> testRunner = new TestRunner<>(
            testStarted(reporter, () -> seed().current()),
            testFailed(reporter, (failure) -> ((FailedWithSeed) failure.getException()).seed()),
            testFinished(reporter, () -> seed().current()));

    @Test
    public void setsTheSeedBeforeEachTestMethodAndThenResetsTheSeedAfterEachTestMethod() throws NoSuchMethodException {
        final long initialSeed = new Random().nextLong();

        testRunner.scheduleTestWithObject(SeededTestClass.class, initialSeed, seed -> seed().next(seed)).run();

        assertThat(reporter.reportFor(SeededTestClass.class, "redTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(1334L)
                .whenTestHasFinished(initialSeed)
                .whenFailed(1334L);

        assertThat(reporter.reportFor(SeededTestClass.class, "greenTest"))
                .didNotFail()
                .beforeTestStarts(initialSeed)
                .duringTest(1334L)
                .whenTestHasFinished(initialSeed);
    }

    @Test
    public void setsTheSeedBeforeEachAnnotatedTestMethodAndThenResetsTheSeedAfterEachTestMethod() {
        final long initialSeed = new Random().nextLong();

        testRunner.scheduleTestWithObject(TestClassWithSeededTestMethods.class, initialSeed, seed -> seed().next(seed)).run();

        assertThat(reporter.reportFor(TestClassWithSeededTestMethods.class, "redTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(9876L)
                .whenTestHasFinished(initialSeed)
                .whenFailed(9876L)
                .failedBecauseOf(AssertionError.class);

        assertThat(reporter.reportFor(TestClassWithSeededTestMethods.class, "greenTest"))
                .didNotFail()
                .beforeTestStarts(initialSeed)
                .duringTest(9371L)
                .whenTestHasFinished(initialSeed);
    }

    @Test
    public void describesTheCurrentSeedWhenTheTestFails() {
        final long initialSeed = new Random().nextLong();

        testRunner.scheduleTestWithObject(NonSeededTestClass.class, initialSeed, seed -> seed().next(seed)).run();

        assertThat(reporter.reportFor(NonSeededTestClass.class, "failingWithAssertionError"))
                .whenFailed(initialSeed)
                .failedBecauseOf(AssertionError.class);

        assertThat(reporter.reportFor(NonSeededTestClass.class, "failingWithAssertionErrorButInitCauseCannotBeInvoked"))
                .whenFailed(initialSeed)
                .failedBecauseOf(AssertionError.class);
    }

    @Test
    public void methodLevelSeedAnnotationsTakePriorityOverClassLevelAnnotations() {
        final long initialSeed = new Random().nextLong();

        testRunner.scheduleTestWithObject(SeededTestClassWithSeededTestMethods.class, initialSeed, seed -> seed().next(seed)).run();

        assertThat(reporter.reportFor(SeededTestClassWithSeededTestMethods.class, "redTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(3891L)
                .whenTestHasFinished(initialSeed)
                .whenFailed(3891L)
                .failedBecauseOf(AssertionError.class);

        assertThat(reporter.reportFor(SeededTestClassWithSeededTestMethods.class, "greenTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(1357L)
                .whenTestHasFinished(initialSeed);
    }

    @Seed(1334)
    public static final class SeededTestClass {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            fail();
        }

        @Test
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
        }
    }

    @Seed(1984)
    public static final class SeededTestClassWithSeededTestMethods {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @Seed(3891)
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            fail();
        }

        @Test
        @Seed(1357)
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
        }
    }

    public static final class TestClassWithSeededTestMethods {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @Seed(9876)
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            fail();
        }

        @Test
        @Seed(9371)
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
        }
    }

    public static final class NonSeededTestClass {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void failingWithAssertionError() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            throw new AssertionError("test fails because of an assertion error");
        }

        @Test
        public void failingWithAssertionErrorButInitCauseCannotBeInvoked() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            throw new AssertionError("test fails because of an assertion error but " +
                    "initCause cannot be invoked on this exception because the initial cause is null", null);
        }

        @Test
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
        }
    }
}
