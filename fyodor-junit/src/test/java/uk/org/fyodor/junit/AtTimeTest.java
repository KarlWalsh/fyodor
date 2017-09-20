package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.AtTime;

import java.time.DateTimeException;
import java.time.LocalTime;

import static java.time.LocalTime.of;
import static uk.org.fyodor.generators.RDG.localTime;
import static uk.org.fyodor.generators.time.LocalTimeRange.now;
import static uk.org.fyodor.generators.time.Timekeeper.current;
import static uk.org.fyodor.generators.time.TimekeeperConfigurer.time;
import static uk.org.fyodor.junit.FyodorTestRule.from;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;

@SuppressWarnings("ConstantConditions")
public final class AtTimeTest {

    private static final Reporter<LocalTime> reporter = reporter();

    private final TestRunner<LocalTime> testRunner = new TestRunner<>(
            testStarted(reporter, () -> current().time()),
            testFailed(reporter, (failure) -> current().time()),
            testFinished(reporter, () -> current().time()));

    @Test
    public void noAnnotationsAndDefaultRule() {
        final LocalTime initialTime = localTime().next();
        Timekeeper.from(time(initialTime));

        testRunner.scheduleTest(NoAnnotationsAndDefaultRule.class).run();

        assertThat(reporter.reportFor(NoAnnotationsAndDefaultRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialTime)
                .duringTest(initialTime)
                .whenTestHasFinished(initialTime);

        assertThat(reporter.reportFor(NoAnnotationsAndDefaultRule.class, "second"))
                .didNotFail()
                .beforeTestStarts(initialTime)
                .duringTest(initialTime)
                .whenTestHasFinished(initialTime);
    }

    @Test
    public void timeConfiguredWithRule() {
        final LocalTime initialTime = localTime().next();
        Timekeeper.from(time(initialTime));

        testRunner.scheduleTest(NoAnnotationsAndConfiguredRule.class).run();

        assertThat(reporter.reportFor(NoAnnotationsAndConfiguredRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialTime)
                .duringTest(of(10, 30, 45))
                .whenTestHasFinished(initialTime);
    }

    @Test
    public void annotatedTestMethods() {
        final LocalTime now = localTime().next();
        Timekeeper.from(time(now));

        testRunner.scheduleTest(AtTimeMethodAnnotation.class).run();

        assertThat(reporter.reportFor(AtTimeMethodAnnotation.class, "first"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(23, 59, 59))
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(AtTimeMethodAnnotation.class, "second"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(0, 0, 0))
                .whenTestHasFinished(now);
    }

    @Test
    public void testFailsWhenTimeStringCannotBeParsed() {
        final LocalTime initialTime = localTime().next();
        Timekeeper.from(time(initialTime));

        testRunner.scheduleTest(BadTimeString.class).run();

        assertThat(reporter.reportFor(BadTimeString.class, "testWithBadTimeString"))
                .beforeTestStarts(initialTime)
                .whenTestHasFinished(initialTime)
                .whenFailed(initialTime)
                .failedBecauseOf(DateTimeException.class);
    }

    public static final class AtTimeMethodAnnotation {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @AtTime("23:59:59")
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
        }

        @Test
        @AtTime("00:00:00")
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
        }
    }

    public static final class BadTimeString {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Test
        @AtTime("this-is-not-a-time")
        public void testWithBadTimeString() {
        }
    }

    public static final class NoAnnotationsAndDefaultRule {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(NoAnnotationsAndDefaultRule.class, testName.getMethodName(), localTime(now()).next());
        }

        @Test
        public void second() {
            reporter.objectDuringTest(NoAnnotationsAndDefaultRule.class, testName.getMethodName(), localTime(now()).next());
        }
    }

    public static final class NoAnnotationsAndConfiguredRule {

        @Rule
        public final FyodorTestRule rule = from(time(10, 30, 45));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
        }
    }
}
