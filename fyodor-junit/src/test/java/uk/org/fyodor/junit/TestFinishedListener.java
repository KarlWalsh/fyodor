package uk.org.fyodor.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

import java.util.function.Supplier;

final class TestFinishedListener<T> extends RunListener {

    private final Reporter<T> reporter;
    private final Supplier<T> obtainObject;

    private TestFinishedListener(final Reporter<T> reporter, final Supplier<T> obtainObject) {
        this.reporter = reporter;
        this.obtainObject = obtainObject;
    }

    @Override
    public void testFinished(final Description description) {
        reporter.objectAfterTestFinishes(description.getTestClass(), description.getMethodName(), obtainObject.get());
    }

    static <T> RunListener testFinished(final Reporter<T> reporter, final Supplier<T> obtainObject) {
        return new TestFinishedListener<>(reporter, obtainObject);
    }
}
