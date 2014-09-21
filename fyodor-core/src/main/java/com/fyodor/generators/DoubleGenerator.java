package com.fyodor.generators;

import com.fyodor.random.RandomValues;
import com.fyodor.range.Range;

final class DoubleGenerator implements Generator<Double> {

    private final RandomValues randomValues;
    private final Range<Double> range;

    DoubleGenerator(final RandomValues randomValues, final Range<Double> range) {
        this.randomValues = randomValues;
        this.range = range;
    }

    @Override
    public Double next() {
        return randomValues.randomDouble(range.lowerBound(), range.upperBound());
    }
}
