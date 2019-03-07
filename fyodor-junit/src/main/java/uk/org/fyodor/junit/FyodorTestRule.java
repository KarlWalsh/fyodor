package uk.org.fyodor.junit;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.junit.rules.RuleChain.outerRule;

public final class FyodorTestRule implements TestRule {

    private final RuleChain delegate;

    public FyodorTestRule() {
        this.delegate = outerRule(new SeedRule());
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return delegate.apply(base, description);
    }
}
