package uk.org.fyodor.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

import java.util.function.Supplier;

final class TestStartedListener<T> extends RunListener {

    private final Reporter<T> reporter;
    private final Supplier<T> obtainObject;

    private TestStartedListener(final Reporter<T> reporter, final Supplier<T> obtainObject) {
        this.reporter = reporter;
        this.obtainObject = obtainObject;
    }

    @Override
    public void testStarted(final Description description) {
        reporter.objectBeforeTestStarts(description.getTestClass(), description.getMethodName(), obtainObject.get());
    }

    static <T> RunListener testStarted(final Reporter<T> reporter, final Supplier<T> obtainObject) {
        return new TestStartedListener<>(reporter, obtainObject);
    }
}
