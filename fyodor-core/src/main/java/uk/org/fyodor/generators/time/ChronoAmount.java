package uk.org.fyodor.generators.time;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public final class ChronoAmount implements Comparable<ChronoAmount> {

    private final ChronoUnit unit;
    private final long amount;

    private ChronoAmount(final ChronoUnit unit, final long amount) {
        this.unit = unit;
        this.amount = amount;
    }

    public <R extends Temporal> R subtractFrom(final R temporal) {
        return unit.addTo(temporal, Math.negateExact(amount));
    }

    @Override
    public final int compareTo(final ChronoAmount that) {
        final ChronoUnit thisUnit = this.unit;
        final ChronoUnit thatUnit = that.unit;

        if (!thisUnit.equals(thatUnit)) {
            throw new IllegalArgumentException(thisUnit + " cannot be compared to " + thatUnit);
        }

        long thatQuantity = that.amount;
        long thisQuantity = this.amount;

        if (thisQuantity == thatQuantity) {
            return 0;
        }

        return thisQuantity < thatQuantity ? -1 : 1;
    }

    @Override
    public final String toString() {
        return String.format("%s %s", amount, unit);
    }

    public static ChronoAmount days(final long numberOfDays) {
        return new ChronoAmount(ChronoUnit.DAYS, numberOfDays);
    }

    public static ChronoAmount years(final long numberOfYears) {
        return new ChronoAmount(ChronoUnit.YEARS, numberOfYears);
    }

    public static ChronoAmount months(final long numberOfMonths) {
        return new ChronoAmount(ChronoUnit.MONTHS, numberOfMonths);
    }
}
