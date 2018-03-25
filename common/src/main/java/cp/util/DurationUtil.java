package cp.util;

import com.google.common.base.Stopwatch;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods related to durations.
 */
public final class DurationUtil {
    private DurationUtil() {
    }

    private static final PeriodFormatter DUR_FMT_CONDENSED = new PeriodFormatterBuilder()
       .printZeroAlways()
       .minimumPrintedDigits(2)
       .appendHours().appendSeparator(":")
       .appendMinutes().appendSeparator(":")
       .appendSecondsWithOptionalMillis()
       .toFormatter();

    private static final PeriodFormatter DUR_FMT = new PeriodFormatterBuilder()
       .appendHours().appendSuffix("hr", "hrs").appendSeparator(" ")
       .appendMinutes().appendSuffix("min").appendSeparator(" ")
       .appendSecondsWithMillis().appendSuffix("s")
       .toFormatter();

    private static final PeriodType TIME_TYPE = PeriodType.dayTime();

    public static String formatCondensed(long duration) {
        return DUR_FMT_CONDENSED.print(new Period(duration, TIME_TYPE));
    }

    public static String formatDuration(long duration) {
        return DUR_FMT.print(new Period(duration, TIME_TYPE));
    }

    public static String formatElapsed(TimeUnit unit, long startTime) {
        return formatDuration(unit.toMillis(System.nanoTime() - startTime));
    }

    public static String formatDuration(Stopwatch timer) {
        long msDur = timer.elapsed(TimeUnit.MILLISECONDS);

        if (msDur < 1000) {
            return timer.toString();
        }

        return formatDuration(msDur);
    }

    private static final PeriodFormatter DUR_WORDS = PeriodFormat.getDefault().withParseType(TIME_TYPE);

    public static String formatDurationVerbose(long duration) {
        return DUR_WORDS.print(new Period(duration, TIME_TYPE));
    }

    public static String formatDurationVerbose(Stopwatch timer) {
        return formatDurationVerbose(timer.elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Rounds the given millisecond timestamp to an equivalent value minus milliseconds.
     *
     * @param millis The milliseconds value to round.
     * @return Value minus seconds.
     */
    public static long roundToSeconds(long millis) {
        return new DateTime(millis).withMillisOfSecond(0).getMillis();
    }

    /**
     * Returns the number of days between the current system time and the given date.
     *
     * @param end The end date to computer number of days for.
     * @return The number of days, or {@link Long#MAX_VALUE} if given date is null.
     */
    public static long daysUntil(@Nullable Date end) {
        if (end == null)
            return Long.MAX_VALUE;

        return TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - end.getTime());
    }

    /**
     * Insulation against Guava version changes
     */
    public static Stopwatch getStopwatchStarted() {
        return Stopwatch.createStarted();
    }

    /**
     * Insulation against Guava version changes
     */
    public static Stopwatch getStopwatchUnstarted() {
        return Stopwatch.createUnstarted();
    }

    public static String getCurrentDateWithMDRDateFormat() {
        return LocalDateTime.now().format(cp.util.CEDate.DEFAULT_DATETIME_FORMATTER);
    }

}
