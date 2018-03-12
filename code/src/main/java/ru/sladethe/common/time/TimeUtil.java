package ru.sladethe.common.time;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class TimeUtil {
    public static final long DAYS_PER_WEEK = 7;

    public static final long HOURS_PER_DAY = 24;
    public static final long HOURS_PER_WEEK = HOURS_PER_DAY * DAYS_PER_WEEK;

    public static final long MINUTES_PER_HOUR = 60;
    public static final long MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;
    public static final long MINUTES_PER_WEEK = MINUTES_PER_DAY * DAYS_PER_WEEK;

    public static final long SECONDS_PER_MINUTE = 60;
    public static final long SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final long SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
    public static final long SECONDS_PER_WEEK = SECONDS_PER_DAY * DAYS_PER_WEEK;

    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * SECONDS_PER_MINUTE;
    public static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * HOURS_PER_DAY;
    public static final long MILLIS_PER_WEEK = MILLIS_PER_DAY * DAYS_PER_WEEK;

    public static final long MICROSECONDS_PER_MILLISECOND = 1000;
    public static final long MICROSECONDS_PER_SECOND = MICROSECONDS_PER_MILLISECOND * MILLIS_PER_SECOND;
    public static final long MICROSECONDS_PER_MINUTE = MICROSECONDS_PER_SECOND * SECONDS_PER_MINUTE;
    public static final long MICROSECONDS_PER_HOUR = MICROSECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final long MICROSECONDS_PER_DAY = MICROSECONDS_PER_HOUR * HOURS_PER_DAY;
    public static final long MICROSECONDS_PER_WEEK = MICROSECONDS_PER_DAY * DAYS_PER_WEEK;

    public static final long NANOSECONDS_PER_MICROSECOND = 1000;
    public static final long NANOSECONDS_PER_MILLISECOND = NANOSECONDS_PER_MICROSECOND * MICROSECONDS_PER_MILLISECOND;
    public static final long NANOSECONDS_PER_SECOND = NANOSECONDS_PER_MILLISECOND * MILLIS_PER_SECOND;
    public static final long NANOSECONDS_PER_MINUTE = NANOSECONDS_PER_SECOND * SECONDS_PER_MINUTE;
    public static final long NANOSECONDS_PER_HOUR = NANOSECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final long NANOSECONDS_PER_DAY = NANOSECONDS_PER_HOUR * HOURS_PER_DAY;
    public static final long NANOSECONDS_PER_WEEK = NANOSECONDS_PER_DAY * DAYS_PER_WEEK;

    private TimeUtil() {
        throw new UnsupportedOperationException();
    }
}
