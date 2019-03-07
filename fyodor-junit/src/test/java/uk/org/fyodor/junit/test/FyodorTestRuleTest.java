package uk.org.fyodor.junit.test;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.junit.FyodorTestRule;
import uk.org.fyodor.random.RandomSourceProvider;
import uk.org.fyodor.testapi.FailedWithSeed;
import uk.org.fyodor.testapi.Seed;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.runner.Description.createTestDescription;

public final class FyodorTestRuleTest {

    private final CapturingStatement<Long> capturingSeed = new CapturingStatement<>(() -> RandomSourceProvider.seed().current());

    @Test
    public void methodAnnotatedWithSeed() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        new FyodorTestRule()
                .apply(capturingSeed, test(annotatedWith(seed(56789L))))
                .evaluate();

        assertThat(capturingSeed.captured()).isEqualTo(56789L);
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void classAnnotatedWithSeed() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        new FyodorTestRule()
                .apply(capturingSeed, test(SeededTestClass.class))
                .evaluate();

        assertThat(capturingSeed.captured()).isEqualTo(1234567890L);
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void methodLevelSeedAnnotationOverridesClassLevelSeedAnnotation() throws Throwable {
        new FyodorTestRule()
                .apply(capturingSeed, test(SeededTestClass.class, annotatedWith(seed(123456L))))
                .evaluate();

        assertThat(capturingSeed.captured()).isEqualTo(123456L);
    }

    @Test
    public void doesNotSetNextSeedWhenTheTestDoesNotHaveAnAnnotatedSeedValue() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        new FyodorTestRule()
                .apply(capturingSeed, test())
                .evaluate();

        assertThat(capturingSeed.captured()).isEqualTo(initialSeed);
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void throwsFailedWithSeedExceptionWhenTestFailsWithException() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        final Exception originalException = new NullPointerException();
        final Exception exceptionCausingTestToFail = new IllegalArgumentException("this is the top-level exception", originalException);

        try {
            new FyodorTestRule()
                    .apply(failingTest(() -> exceptionCausingTestToFail), test())
                    .evaluate();

            fail("This test should have thrown an exception");
        } catch (final FailedWithSeed exception) {
            assertThat(exception.seed()).isEqualTo(initialSeed);
            assertThat(exception).hasCause(exceptionCausingTestToFail);
        }
    }

    @Test
    public void revertsToPreviousSeedWhenTestFinishes() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        new FyodorTestRule()
                .apply(capturingSeed, test(annotatedWith(seed(567123L))))
                .evaluate();

        assertThat(RandomSourceProvider.seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void doesNotRevertSeedIfSeedWasNotSetByTest() throws Throwable {
        final long firstSeed = 1L;
        final long secondSeed = 2L;

        RandomSourceProvider.seed().next(firstSeed);
        RandomSourceProvider.seed().next(secondSeed);

        new FyodorTestRule()
                .apply(capturingSeed, test())
                .evaluate();

        assertThat(RandomSourceProvider.seed().current()).isEqualTo(secondSeed);
    }

    private static Annotation annotatedWith(final Annotation annotation) {
        return annotation;
    }

    private static Seed seed(final long seed) {
        return new Seed() {
            @Override
            public long value() {
                return seed;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Seed.class;
            }
        };
    }

    private static Description test() {
        return test(StandardTestClass.class);
    }

    private static Description test(final Class<?> testClass) {
        return createTestDescription(testClass, "test");
    }

    private static Description test(final Annotation annotation) {
        return test(StandardTestClass.class, annotation);
    }

    private static Description test(final Class<?> testClass, final Annotation annotation) {
        return createTestDescription(testClass, "test", annotation);
    }

    private static Statement failingTest(final Supplier<Throwable> reasonForFailure) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                throw reasonForFailure.get();
            }
        };
    }

    @SuppressWarnings("WeakerAccess")
    public static final class StandardTestClass {
    }

    @SuppressWarnings("WeakerAccess")
    @Seed(1234567890)
    public static final class SeededTestClass {
    }

    static final class CapturingStatement<T> extends Statement {

        private final Supplier<T> supplierOfT;
        private T captured;

        CapturingStatement(final Supplier<T> supplierOfT) {
            this.supplierOfT = supplierOfT;
        }

        T captured() {
            return captured;
        }

        @Override
        public void evaluate() {
            this.captured = supplierOfT.get();
        }
    }
}