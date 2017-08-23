package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.RDG;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.AtDate;
import uk.org.fyodor.testapi.AtTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalDate.of;
import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertTrue;
import static uk.org.fyodor.generators.RDG.localDateTime;
import static uk.org.fyodor.generators.RDG.localTime;
import static uk.org.fyodor.generators.time.LocalDateRange.today;
import static uk.org.fyodor.generators.time.LocalTimeRange.now;
import static uk.org.fyodor.generators.time.Timekeeper.current;
import static uk.org.fyodor.generators.time.TimekeeperConfigurer.*;
import static uk.org.fyodor.junit.FyodorTestRule.from;
import static uk.org.fyodor.junit.FyodorTestRule.fyodorTestRule;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;

@SuppressWarnings("ConstantConditions")
public final class AtDateTimeTest {

    private static final Reporter<LocalDateTime> reporter = reporter();

    private final TestRunner<LocalDateTime> testRunner = new TestRunner<>(
            testStarted(reporter, () -> current().dateTime()),
            testFailed(reporter, (failure) -> current().dateTime()),
            testFinished(reporter, () -> current().dateTime()));

    @Test
    public void noAnnotationsWithDefaultRule() {
        final LocalDateTime initialDateTime = localDateTime().next();
        Timekeeper.from(dateAndTime(initialDateTime));

        testRunner
                .scheduleTest(NoAnnotationsWithDefaultRule.class)
                .run();

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(initialDateTime)
                .whenTestHasFinished(initialDateTime);

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "second"))
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(initialDateTime)
                .whenTestHasFinished(initialDateTime);
    }

    @Test
    public void timeConfiguredInRule() {
        final LocalDate initialDate = RDG.localDate().next();
        final LocalDateTime initialDateTime = initialDate.atTime(localTime().next());
        Timekeeper.from(dateAndTime(initialDateTime));

        testRunner.scheduleTest(RuleConfiguredAtTime.class).run();

        assertThat(reporter.reportFor(RuleConfiguredAtTime.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(initialDate.atTime(10, 30, 45))
                .whenTestHasFinished(initialDateTime);
    }

    @Test
    public void dateConfiguredInRule() {
        final LocalDate initialDate = RDG.localDate().next();
        final LocalTime initialTime = RDG.localTime().next();
        final LocalDateTime initialDateTime = initialDate.atTime(initialTime);
        Timekeeper.from(dateAndTime(initialDateTime));

        testRunner.scheduleTest(RuleConfiguredWithDate.class).run();

        assertThat(reporter.reportFor(RuleConfiguredWithDate.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(of(1999, 12, 31).atTime(initialTime))
                .whenTestHasFinished(initialDateTime);
    }

    @Test
    public void dateAndTimeConfiguredInRule() {
        final LocalDateTime initialDateTime = localDateTime().next();
        Timekeeper.from(dateAndTime(initialDateTime));

        testRunner.scheduleTest(RuleConfiguredWithDateAndTime.class).run();

        assertThat(reporter.reportFor(RuleConfiguredWithDateAndTime.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(of(1999, 12, 31, 23, 59, 59))
                .whenTestHasFinished(initialDateTime);
    }


    @Test
    public void resetsBackToPreviousDateAndTimeAfterEachTestMethod() {
        final LocalDateTime now = localDateTime().next();
        Timekeeper.from(dateAndTime(now));

        testRunner.scheduleTest(DateAndTimeConfiguredInAnnotationsAndRule.class).run();

        assertThat(reporter.reportFor(DateAndTimeConfiguredInAnnotationsAndRule.class, "annotatedClass"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(2010, 1, 1, 12, 0, 0))
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(DateAndTimeConfiguredInAnnotationsAndRule.class, "annotatedMethod"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(2015, 6, 15, 17, 1, 30))
                .whenTestHasFinished(now);
    }

    public static final class NoAnnotationsWithDefaultRule {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDateTime().next());
        }

        @Test
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDateTime().next());
        }
    }

    @AtDate("2011-04-13")
    @AtTime("16:02:31")
    public static final class TestClassWithDateTimeSpecificationOnClass {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDateTime(today(), now()).next());
        }

        @Test
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDateTime(today(), now()).next());
            assertTrue(false);
        }
    }


    public static final class RuleConfiguredAtTime {

        @Rule
        public final FyodorTestRule rule = from(time(10, 30, 45));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDateTime(today(), now()).next());
        }
    }

    public static final class RuleConfiguredWithDate {

        @Rule
        public final FyodorTestRule rule = from(date(1999, 12, 31));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDateTime(today(), now()).next());
        }
    }

    public static final class RuleConfiguredWithDateAndTime {

        @Rule
        public final FyodorTestRule rule = from(dateAndTime(
                1999, 12, 31,
                23, 59, 59));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDateTime(today(), now()).next());
        }
    }

    @AtDate("2010-01-01")
    @AtTime("12:00:00")
    public static final class DateAndTimeConfiguredInAnnotationsAndRule {

        @Rule
        public final FyodorTestRule rule = from(dateAndTime(
                1999, 12, 31,
                23, 59, 59));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void annotatedClass() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDateTime(today(), now()).next());
        }

        @Test
        @AtDate("2015-06-15")
        @AtTime("17:01:30")
        public void annotatedMethod() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDateTime(today(), now()).next());
        }
    }
}
