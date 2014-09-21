package com.fyodor.generators;

import com.fyodor.random.RandomValues;

final class BooleanGenerator implements Generator<Boolean> {

    private final RandomValues randomValues;

    BooleanGenerator(final RandomValues randomValues) {
        this.randomValues = randomValues;
    }

    @Override
    public Boolean next() {
        return randomValues.randomBoolean();
    }
}
