package com.trilemon.commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.LocalTime;

/**
 * @author kevin
 */
public class LocalTimeInterval {
    private static final Instant CONSTANT = new Instant(0);
    private final LocalTime from;
    private final LocalTime to;

    public LocalTimeInterval(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }

    public LocalTimeInterval(int fromHour, int toHour) {
        this(new LocalTime(fromHour, 0), new LocalTime(toHour, 0));
    }

    public boolean isValid() {
        try {
            return toInterval() != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean overlapsWith(LocalTimeInterval localTimeInterval) {
        return this.toInterval().overlaps(localTimeInterval.toInterval());
    }

    /**
     * @return this represented as a proper Interval
     * @throws IllegalArgumentException if invalid (to is before from)
     */
    private Interval toInterval() throws IllegalArgumentException {
        return new Interval(from.toDateTime(CONSTANT), to.toDateTime(CONSTANT));
    }

    public LocalTime getFrom() {
        return from;
    }

    public LocalTime getTo() {
        return to;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
