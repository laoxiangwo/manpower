package cp.util;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * Should only be used for dates that are uncertain!!!
 *
 * This class is used to parse dates received from external sources into a fully valid date and store the precision to
 * which we are confident this date to be correct
 *
 * Parsing works on a cascading basis, if any element of the date is found to be invalid everything following is invalid as well
 * e.g. if we have 1990-00-15, it will be parsed as if we just received 1990 even if 15 would be a valid day since the month is invalid
 */
public final class CEDate {
    public final static int MDR_DATE_MIN_YEAR = 1800;
    public final static int MDR_DATE_MAX_YEAR = 2050;
    private static final Logger logger = LoggerFactory.getLogger(CEDate.class);

    private final LocalDateTime dateTime;
    private final TimePrecision precision;

    // all dates should end up in the ISO-8601 format
    public final static DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final static DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final static DateTimeFormatter PARSING_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
    private final static DateTimeFormatter PARSING_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d");

    // the builder only requires date to be passed, for the rest it defaults to earliest date and then attempts to
    // match against the known formats
    public static class MDRDateBuilder {
        private String date;
        private DateFormat format;
        private boolean latest;

        public MDRDateBuilder() {
            format = null;
        }

        public MDRDateBuilder parseDate(String date) {
            this.date = date;
            return this;
        }

        public MDRDateBuilder format(DateFormat format) {
            this.format = format;
            return this;
        }

        public MDRDateBuilder latest(boolean latest) {
            this.latest = latest;
            return this;
        }

        public CEDate build() {
            return parse(date, format, latest);
        }
    }

    private CEDate(LocalDateTime dateTime, TimePrecision precision) {
        this.dateTime = dateTime;
        this.precision = precision;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public TimePrecision getPrecision() {
        return precision;
    }

    public String printLocalDateTime() {
        return dateTime.format(DEFAULT_DATETIME_FORMATTER);
    }

    public String printLocalDate() {
        return dateTime.format(DEFAULT_DATE_FORMATTER);
    }

    public String printPrecision() {
        return precision.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CEDate other = (CEDate) o;
        switch (precision) {
            case TIMESTAMP:
                return Objects.equal(dateTime, other.dateTime) &&
                        Objects.equal(precision, other.precision);
            case DAY:
                return Objects.equal(dateTime.getYear(), other.dateTime.getYear()) &&
                       Objects.equal(dateTime.getMonth(), other.dateTime.getMonth()) &&
                       Objects.equal(dateTime.getDayOfMonth(), other.dateTime.getDayOfMonth()) &&
                       Objects.equal(precision, other.precision);
            case MONTH:
                return Objects.equal(dateTime.getYear(), other.dateTime.getYear()) &&
                        Objects.equal(dateTime.getMonth(), other.dateTime.getMonth()) &&
                        Objects.equal(precision, other.precision);
            case YEAR:
                return Objects.equal(dateTime.getYear(), other.dateTime.getYear()) &&
                        Objects.equal(precision, other.precision);
            case RESERVED:
            default:
                return false;
        }
    }

    @Override
    public int hashCode() {
        switch (precision) {
            case TIMESTAMP:
                return Objects.hashCode(dateTime);
            case DAY:
                return Objects.hashCode(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth());
            case MONTH:
                return Objects.hashCode(dateTime.getYear(), dateTime.getMonth());
            case YEAR:
                return Objects.hashCode(dateTime.getYear());
            case RESERVED:
            default:
                return Objects.hashCode(dateTime, 12345); // just to be different from above
        }
    }

    /**
     * Check if this date is strictly before another date
     *
     * @param date - date to check
     * @param resolveIntervalAsEarliest - specifies how dates with higher precision (intervals compared to other dates),
     *                                  are to be treated with regards to dates of higher precision, they should never
     *                                  be treated as same, by default (false) they go to the back, otherwise they go to the front
     * @return true if this is before the other date
     */
    public boolean isBefore(CEDate date, boolean resolveIntervalAsEarliest) {
        // if this date has lower precision than the date passed in
        if (precision.getValue() < date.getPrecision().getValue() && equalToPrecision(date, precision)) {
            logger.debug("Dates equal to lower precision, returning based on flag set");
            return resolveIntervalAsEarliest;
        }

        // if this date has higher precision than the date passed in
        if (precision.getValue() > date.getPrecision().getValue() && equalToPrecision(date, date.getPrecision())) {
            logger.debug("Dates equal to lower precision, returning based on flag set");
            return !resolveIntervalAsEarliest;
        }

        // precision is equal or the dates are not equal to the smaller of the two precisions
        logger.debug("Dates not equal or equally precise, calling LocalDateTime isBefore()");
        return dateTime.isBefore(date.getDateTime());
    }

    /**
     * Check if this date is strictly after another date
     *
     * @param date - date to check
     * @param resolveIntervalAsEarliest - specifies how dates with higher precision (intervals compared to other dates),
     *                                  are to be treated with regards to dates of higher precision, they should never
     *                                  be treated as same, by default (false) they go to the back, otherwise they go to the front
     * @return true if this is after the other date
     */
    public boolean isAfter(CEDate date, boolean resolveIntervalAsEarliest) {
        // if this date has lower precision than the date passed in
        if (precision.getValue() < date.getPrecision().getValue() && equalToPrecision(date, precision)) {
            logger.debug("Dates equal to lower precision, returning based on flag set");
            return !resolveIntervalAsEarliest;
        }

        // if this date has higher precision than the date passed in
        if (precision.getValue() > date.getPrecision().getValue() && equalToPrecision(date, date.getPrecision())) {
            logger.debug("Dates equal to lower precision, returning based on flag set");
            return resolveIntervalAsEarliest;
        }

        // precision is equal or the dates are not equal to the smaller of the two precisions
        logger.debug("Dates not equal or equally precise, calling LocalDateTime isBefore()");
        return dateTime.isAfter(date.getDateTime());
    }

    /**
     * Check if this date is during (or is equal to) another date. This is possible since we can treat dates as intervals
     * based on their precision.
     * Eg. 2015-01-01 with precision MONTH will be treated as interval (2015-01-01, 2015-01-31), hence a date such as
     * 2015-01-14 (precision DAY) will be during the previous date
     *      '2015-01-15'.isDuring('2015-01') = true
     *      '2015-01'.isDuring('2015-01-15') = false
     * If two dates are equal, they are during each other
     *
     * @param date - date to check
     * @return true if this is during (or is equal to) the other date
     */
    public boolean isDuring(CEDate date) {
        // if this date has lower precision than the date passed in
        if (precision.getValue() < date.getPrecision().getValue() && equalToPrecision(date, precision)) {
            logger.debug("Dates equal to lower precision, this date is a larger interval. Returning false.");
            return false;
        }

        // if this date has higher precision than the date passed in
        if (precision.getValue() > date.getPrecision().getValue() && equalToPrecision(date, date.getPrecision())) {
            logger.debug("Dates equal to lower precision, this date is a smaller interval. Returning true.");
            return true;
        }

        // precision is equal or the dates are not equal to the smaller of the two precisions
        logger.debug("Dates have equal precision or they are not overlapping, can be during only if they are equal");
        return dateTime.equals(date.getDateTime());
    }

    /**
     * Check if this date contains (or is equal to) another date. This is possible since we can treat dates as intervals
     * based on their precision.
     * Eg. 2015-01-01 with precision MONTH will be treated as interval (2015-01-01, 2015-01-31), hence a date such as
     * 2015-01-14 (precision DAY) will be contained within the previous date
     *      '2015-01'.contains('2015-01-15') = true
     *      '2015-01-15'.contains('2015-01') = false
     * If two dates are equal, they contain each other
     *
     * @param date - date to check
     * @return true if this contains (or is equal to) the other date
     */
    public boolean contains(CEDate date) {
        // if this date has lower precision than the date passed in
        if (precision.getValue() < date.getPrecision().getValue() && equalToPrecision(date, precision)) {
            logger.debug("Dates equal to lower precision, this date is a smaller interval. Returning true.");
            return true;
        }

        // if this date has higher precision than the date passed in
        if (precision.getValue() > date.getPrecision().getValue() && equalToPrecision(date, date.getPrecision())) {
            logger.debug("Dates equal to lower precision, this date is a larger interval. Returning true.");
            return false;
        }

        // precision is equal or the dates are not equal to the smaller of the two precisions
        logger.debug("Dates have equal precision or they are not overlapping, can contain only if they are equal");
        return dateTime.equals(date.getDateTime());
    }

    /**
     * Compare this date to another up until a certain precision
     *
     * @param date - date to check
     * @param precision - precision to which the dates should be compared for equality
     * @return true if the dates are equal up until the specified precision
     */
    private boolean equalToPrecision(CEDate date, TimePrecision precision) {
        if (dateTime.getYear() != date.getDateTime().getYear())
            return false;

        if(precision.getValue() >= TimePrecision.MONTH.getValue() &&
           dateTime.getMonth() != date.getDateTime().getMonth())
            return false;

        if(precision.getValue() >= TimePrecision.DAY.getValue() &&
           dateTime.getDayOfMonth() != date.getDateTime().getDayOfMonth())
            return false;

        if(precision.getValue() >= TimePrecision.TIMESTAMP.getValue() &&
           dateTime.getHour() != date.getDateTime().getHour() &&
           dateTime.getMinute() != date.getDateTime().getMinute() &&
           dateTime.getSecond() != date.getDateTime().getSecond())
            return false;

        return true;
    }

    // this is meant for cases where CEDate is being reconstructed after previously saved in the DB
    // the string should be a fully qualified dateTime or date in the default format
    // if the parsing fails the string will go through regular parsing as any other unknown date string might
    // in which case the precision passed might not match the result
    @Nullable
    public static CEDate parse(String dateTime, String precisionString) {
        TimePrecision precision;
        try {
            precision = TimePrecision.valueOf(precisionString);
        } catch (Exception e) {
            logger.debug("Precision {} not valid, attempting to parse date on its own", precisionString);
            return parse(dateTime);
        }

        if (Strings.isNullOrEmpty(dateTime)) {
            logger.debug("Passed in string found to be null or empty, returning null");
            return null;
        }

        LocalDateTime localDateTime = null;

        // see if date can be parsed as a LocalDateTime in default format
        logger.debug("Trying to parse as fully valid dateTime");
        try {
            localDateTime = LocalDateTime.parse(dateTime, DEFAULT_DATETIME_FORMATTER);
        } catch (Exception e) {
            logger.debug("String {} not a valid dateTime in default format", dateTime);
        }

        if (localDateTime != null)
            return new CEDate(localDateTime, precision);

        // above failed, see if date can be parsed as a LocalDate in default format
        logger.debug("Trying to parse as fully valid date");
        try {
            localDateTime = LocalDateTime.of(LocalDate.parse(dateTime, DEFAULT_DATE_FORMATTER), LocalTime.MIN);
        } catch (Exception e) {
            logger.debug("String {} not a valid date in default format", dateTime);
        }

        if (localDateTime != null)
            return new CEDate(localDateTime, precision.getValue() > TimePrecision.DAY.getValue() ? TimePrecision.DAY : precision);

        // dateTime cannot be parsed correctly, precision is worthless, re-parse
        logger.debug("Regular parsing of {} failed. Attempting to parse as an unknown string, precision might not be the same.", dateTime);
        return parse(dateTime);
    }

    @Nullable
    public static CEDate parse(String original) {
        return parse(original, null, false);
    }

    @Nullable
    public static CEDate parseWithDefaultFormat(String original) {
        return parse(original, DateFormat.YYYY_MM_DD, false);
    }

    @Nullable
    public static CEDate parse(String original, boolean latest) {
        return parse(original, null, latest);
    }

    // attempts to parse the date string, and figure out what precision should be assigned to the resulting date,
    // it always returns a valid date if possible, otherwise null
    // format can be null, and in that case we will attempt to match the date string against the possible formats,
    // if a format is not provided and cannot be matched, return null
    @Nullable
    public static CEDate parse(String original, @Nullable DateFormat format, boolean latest) {
        TimePrecision precision = TimePrecision.YEAR;

        String yearStr = null;
        String monthStr = null;
        String dayStr = null;
        String hourStr = null;
        String minuteStr = null;
        String secondStr = null;

        StringBuilder stringBuilder = new StringBuilder();

        if (Strings.isNullOrEmpty(original)) {
            logger.debug("Passed in string found to be null or empty, returning null");
            return null;
        } else {
            if(format == null) {
                //attempt to determine the format if it was not provided, return null if unsuccessful
                logger.debug("Attempting to determine format since none was specified");
                format = determineFormat(original);

                if(format == null) {
                    logger.error("Format could not be determined, returning null");
                    return null;
                }

                logger.trace("Format determined to be {}", format);
            }

            Matcher dateMatcher;
            if ((dateMatcher = format.getPattern().matcher(original)).matches()) {
                yearStr = dateMatcher.group(format.getYearGroup());
                if(format.getMonthGroup() != 0)
                    monthStr = dateMatcher.group(format.getMonthGroup());
                if(format.getDayGroup() != 0)
                    dayStr = dateMatcher.group(format.getDayGroup());
                if(format.getHourGroup() != 0)
                    hourStr = dateMatcher.group(format.getHourGroup());
                if(format.getMinuteGroup() != 0)
                    minuteStr = dateMatcher.group(format.getMinuteGroup());
                if(format.getSecondGroup() != 0)
                    secondStr = dateMatcher.group(format.getSecondGroup());
            } else {
                // failed to match
                logger.error("Matching against the format failed. String date: {}, format: {}", original, format);
                return null;
            }

            // parsing fails if year is not set
            if (Strings.isNullOrEmpty(yearStr) || !isYearLegal(yearStr)) {
                logger.error("Year {} found to be null, empty or invalid. Date cannot be determined to any precision. Returning null", yearStr);
                return null;
            }

            stringBuilder.append(yearStr);

            if (!Strings.isNullOrEmpty(monthStr) && isMonthLegal(monthStr)) {
                stringBuilder.append("-").append(monthStr);
                precision = TimePrecision.MONTH;

                if (!Strings.isNullOrEmpty(dayStr) && isDayLegal(dayStr, stringBuilder.toString())) {
                    precision = TimePrecision.DAY;
                    stringBuilder.append("-").append(dayStr);

                    if (!Strings.isNullOrEmpty(hourStr) && isHourLegal(hourStr)) {
                        stringBuilder.append(" ").append(hourStr);

                        if (!Strings.isNullOrEmpty(minuteStr) && isMinuteLegal(minuteStr)) {
                            stringBuilder.append(":").append(minuteStr);

                            if (!Strings.isNullOrEmpty(secondStr) && isSecondLegal(secondStr)) {
                                precision = TimePrecision.TIMESTAMP;
                                stringBuilder.append(":").append(secondStr);
                            } else {
                                stringBuilder.append(":00");
                            }
                        } else {
                            stringBuilder.append(":00:00");
                        }
                    } else {
                        stringBuilder.append(" 00:00:00");
                    }
                } else {
                    stringBuilder.append("-01 00:00:00");
                }
            } else {
                stringBuilder.append("-01-01 00:00:00");
            }
        }

        LocalDateTime localDateTime = LocalDateTime.parse(stringBuilder.toString(), PARSING_DATETIME_FORMATTER);

        if (latest)
            localDateTime = getIntervalEnd(localDateTime, precision);

        logger.debug("Date parsed to {}, with precision {}", localDateTime.format(DEFAULT_DATETIME_FORMATTER), precision);

        return new CEDate(localDateTime, precision);
    }

    // tries to match a string against the known formats, return null if unsuccessful
    @Nullable
    private static DateFormat determineFormat(String original) {
        if ((DateFormat.YYYY_MM_DD.getPattern().matcher(original)).matches()) {
            return DateFormat.YYYY_MM_DD;
        } else if ((DateFormat.YYYYMM.getPattern().matcher(original)).matches()) {
            return DateFormat.YYYYMM;
        } else if ((DateFormat.YYYYMMDD.getPattern().matcher(original)).matches()) {
            return DateFormat.YYYYMMDD;
        } else if ((DateFormat.YYYY_MM.getPattern().matcher(original)).matches()) {
            return DateFormat.YYYY_MM;
        } else if ((DateFormat.MM_DD_YYYY.getPattern().matcher(original)).matches()) {
            return DateFormat.MM_DD_YYYY;
        } else if ((DateFormat.MMDDYYYY.getPattern().matcher(original)).matches()) {
            return DateFormat.MMDDYYYY;
        } else if ((DateFormat.DDMMYYYY.getPattern().matcher(original)).matches()) {
            return DateFormat.DDMMYYYY;
        } else if ((DateFormat.MM_YYYY.getPattern().matcher(original)).matches()) {
            return DateFormat.MM_YYYY;
        } else if ((DateFormat.YYYY.getPattern().matcher(original)).matches()) {
            return DateFormat.YYYY;
        } else {
            // failed to match to a known pattern
            return null;
        }
    }

    private static boolean isYearLegal(String yearStr) {
        try {
            int year = Integer.parseInt(yearStr);
            return year >= MDR_DATE_MIN_YEAR && year <= MDR_DATE_MAX_YEAR;
        } catch (Exception e) {
            logger.debug("String representation of year was found to be invalid, string: {}", yearStr);
            return false;
        }
    }

    private static boolean isMonthLegal(String monthStr) {
        try {
            int month = Integer.parseInt(monthStr);
            return month > 0 && month <= 12;
        } catch (Exception e) {
            logger.debug("String representation of month was found to be invalid, string: {}", monthStr);
            return false;
        }
    }

    private static boolean isDayLegal(String dayStr, String yearMonth) {
        try {
            LocalDate.parse(yearMonth + "-" + dayStr, PARSING_DATE_FORMATTER);
            return true;
        } catch (Exception e) {
            logger.debug("String representation of day was found to be invalid, string: {}", dayStr);
            return false;
        }
    }

    @Nullable
    public static CEDate fromLocalDateTime(LocalDateTime timestamp) {
        return new CEDate(timestamp, TimePrecision.TIMESTAMP);
    }

    public static CEDate fromLocalDate(LocalDate date) {
        return new CEDate(LocalDateTime.of(date, LocalTime.MIDNIGHT), TimePrecision.DAY);
    }

    public boolean isAfter(CEDate date) {
        return this.dateTime.isAfter(date.dateTime);
    }

    public boolean isBefore(CEDate date) {
        return this.dateTime.isBefore(date.dateTime);
    }

    public String toString() {
       switch (precision) {
           case YEAR :
               return dateTime.getYear() + "-00-00";
           case MONTH :
               return dateTime.getYear() + "-" +
                       (dateTime.getMonth().getValue() > 10 ? dateTime.getMonth().getValue() : "0" + dateTime.getMonth().getValue()) +
                       "-00";
           case DAY:
               return dateTime.toLocalDate().toString();
           case TIMESTAMP:
           default:
               return DEFAULT_DATETIME_FORMATTER.format(dateTime);
       }
    }

    private static boolean isHourLegal(String hourStr) {
        try {
            int month = Integer.parseInt(hourStr);
            return month > 0 && month <= 24;
        } catch (Exception e) {
            logger.debug("String representation of hour was found to be invalid, string: {}", hourStr);
            return false;
        }
    }

    private static boolean isMinuteLegal(String minuteStr) {
        try {
            int month = Integer.parseInt(minuteStr);
            return month > 0 && month <= 60;
        } catch (Exception e) {
            logger.debug("String representation of minute was found to be invalid, string: {}", minuteStr);
            return false;
        }
    }

    private static boolean isSecondLegal(String secondStr) {
        try {
            int month = Integer.parseInt(secondStr);
            return month > 0 && month <= 60;
        } catch (Exception e) {
            logger.debug("String representation of second was found to be invalid, string: {}", secondStr);
            return false;
        }
    }

    private static LocalDateTime getIntervalEnd(LocalDateTime original, TimePrecision precision) {
        LocalDateTime result = original;

        if(precision.getValue() < TimePrecision.MONTH.getValue()) {
            result = result.withMonth(12);
        }
        if(precision.getValue() < TimePrecision.DAY.getValue()) {
            result = result.with(result.getMonth()).with(lastDayOfMonth());
        }
        if(precision.getValue() < TimePrecision.TIMESTAMP.getValue()) {
            result = LocalDateTime.of(result.toLocalDate(), LocalTime.MAX);
        }
        /*if(precision.getValue() < TimePrecision.HOUR.getValue()) {
            result = result.withHour(23);
        }
        if(precision.getValue() < TimePrecision.MINUTE.getValue()) {
            result = result.withMinute(59);
        }
        if(precision.getValue() < TimePrecision.SECOND.getValue()) {
            result = result.withSecond(59);
        }*/

        return result;
    }

    /**
     * Get latest date of a CEDate.
     * Eg. 2015     -> 2015-12-31
     *     2015-4   -> 2015-4-30
     *     2015-4-4 -> 2015-4-4
     * @param original CEDate
     * @return CEDate new date altered to be the middle date
     */
    public static CEDate getIntervalEnd(CEDate original) {
        LocalDateTime result = original.getDateTime();
        TimePrecision precision = original.getPrecision();

        if(precision.getValue() < TimePrecision.MONTH.getValue()) {
            result = result.withMonth(12);
        }
        if(precision.getValue() < TimePrecision.DAY.getValue()) {
            result = result.with(result.getMonth()).with(lastDayOfMonth());
        }

        return new CEDate(result, TimePrecision.DAY);
    }

    /**
     * Get middle date of a CEDate.
     * Eg. 2015     -> 2015-6-15
     *     2015-4   -> 2015-4-15
     *     2015-4-4 -> 2015-4-4
     * @param original CEDate
     * @return CEDate new date altered to be the middle date
     */
    public static CEDate getIntervalMedian(CEDate original) {
        LocalDateTime result = original.getDateTime();
        TimePrecision precision = original.getPrecision();

        if(precision.getValue() < TimePrecision.MONTH.getValue()) {
            result = result.withMonth(6);
        }
        if(precision.getValue() < TimePrecision.DAY.getValue()) {
            result = result.with(result.getMonth()).withDayOfMonth(15);
        }

        return new CEDate(result, TimePrecision.DAY);
    }

    /**
     * Get first date of a CEDate.
     * Eg. 2015     -> 2015-1-1
     *     2015-4   -> 2015-4-1
     *     2015-4-4 -> 2015-4-4
     * @param original CEDate
     * @return CEDate new date altered to be the middle date
     */
    public static CEDate getIntervalStart(CEDate original) {
        LocalDateTime result = original.getDateTime();
        TimePrecision precision = original.getPrecision();

        if(precision.getValue() < TimePrecision.MONTH.getValue()) {
            result = result.withMonth(1);
        }
        if(precision.getValue() < TimePrecision.DAY.getValue()) {
            result = result.with(result.getMonth()).withDayOfMonth(1);
        }

        return new CEDate(result, TimePrecision.DAY);
    }

    /*private static LocalDateTime getEarliestDate(LocalDateTime original, TimePrecision precision) {
        LocalDateTime result = original;

        if(precision.getValue() < TimePrecision.MONTH.getValue()) {
            result = result.withMonth(1);
        }
        if(precision.getValue() < TimePrecision.DAY.getValue()) {
            result = result.withDayOfMonth(1);
        }
        if(precision.getValue() < TimePrecision.TIMESTAMP.getValue()) {
            result = LocalDateTime.of(result.toLocalDate(), LocalTime.MIN);
        }
        *//*if(precision.getValue() < TimePrecision.HOUR.getValue()) {
            result = result.withHour(0);
        }
        if(precision.getValue() < TimePrecision.MINUTE.getValue()) {
            result = result.withMinute(0);
        }
        if(precision.getValue() < TimePrecision.SECOND.getValue()) {
            result = result.withSecond(0);
        }*//*

        return result;
    }*/

    public enum TimePrecision {
        /*SECOND    (111111),
        MINUTE      (111110),
        HOUR        (111100),*/
        TIMESTAMP   (111100),
        DAY         (111000),
        MONTH       (110000),
        YEAR        (100000),
        RESERVED    (000000);

        private int value;

        TimePrecision(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Patterns for parsing dates, based on the patters from DWDate
     *
     * Additional checks in the code will check that the date is valid
     * To create a pattern use these
     *
     * Delimiters, if any:              [_/-]
     *
     * Year
     * YYYY, if delimiters present:     (\\d{4})
     * YYYY, if delimiters NOT present: ((18|19|20)\\d{2})
     * YY:                              (\\d{2})
     *
     * Month:                           (0\\d|1[0-2]|\\d)
     *
     * Day:                             (\\d{1,2})
     */
    public enum DateFormat {
        YYYY_MM_DD("(\\d{4})[_/-](0\\d|1[0-2]|\\d)[_/-](\\d{1,2})( .*)?", 1, 2, 3, 0, 0, 0),
        YYYYMMDD("((18|19|20)\\d{2})(0\\d|1[0-2]|\\d)(\\d{1,2})( .*)?", 1, 3, 4, 0, 0, 0),
        MM_DD_YYYY("(0\\d|1[0-2]|\\d)[/_-](\\d{1,2})[/_-](\\d{4})( .*)?", 3, 1, 2, 0, 0, 0),
        MMDDYYYY("(0\\d|1[0-2]|\\d)(\\d{1,2})(\\d{4})( .*)?", 3, 1, 2, 0, 0, 0),
        DDMMYYYY("(\\d{1,2})(0\\d|1[0-2]|\\d)((18|19|20)\\d{2})( .*)?", 3, 2, 1, 0, 0, 0),
        YYYY_MM("(\\d{4})[_/-](0\\d|1[0-2]|\\d)( .*)?", 1, 2, 0, 0, 0, 0),
        YYYYMM("((18|19|20)\\d{2})(0\\d|1[0-2]|\\d)( .*)?", 1, 3, 0, 0, 0, 0),
        MM_YYYY("(0\\d|1[0-2]|\\d)[_/-](\\d{4})( .*)?", 2, 1, 0, 0, 0, 0),
        YYYY("(\\d{4})( .*)?", 1, 0, 0, 0, 0, 0);

        private Pattern pattern;
        private int yearGroup;
        private int monthGroup;
        private int dayGroup;
        private int hourGroup;
        private int minuteGroup;
        private int secondGroup;

        DateFormat(String regex, int yearGroup, int monthGroup, int dayGroup,
                   int hourGroup, int minuteGroup, int secondGroup) {
            this.pattern = Pattern.compile(regex);
            this.yearGroup = yearGroup;
            this.monthGroup = monthGroup;
            this.dayGroup = dayGroup;
            this.hourGroup = hourGroup;
            this.minuteGroup = minuteGroup;
            this.secondGroup = secondGroup;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public int getYearGroup() {
            return yearGroup;
        }

        public int getMonthGroup() {
            return monthGroup;
        }

        public int getDayGroup() {
            return dayGroup;
        }

        public int getHourGroup() {
            return hourGroup;
        }

        public int getMinuteGroup() {
            return minuteGroup;
        }

        public int getSecondGroup() {
            return secondGroup;
        }
    }

    public static final Comparator<CEDate> MDR_DATE_COMPARATOR_INTERVAL_START_NO_NULLS = new Comparator<CEDate>() {
        @Override
        public int compare(CEDate left, CEDate right) {
            return left.isBefore(right, true) ? -1 :
                    left.isAfter(right, true) ? 1 :
                    left.precision.value > right.precision.value ? 1 :
                    left.precision.value < right.precision.value ? -1 :
                    0;
        }
    };

    public static final Comparator<CEDate> MDR_DATE_COMPARATOR_INTERVAL_END_NO_NULLS = new Comparator<CEDate>() {
        @Override
        public int compare(CEDate left, CEDate right) {
            return left.isBefore(right, false) ? -1 :
                    left.isAfter(right, false) ? 1 :
                            left.precision.value > right.precision.value ? -1 :
                                    left.precision.value < right.precision.value ? 1 :
                                            0;
        }
    };

    public static final Comparator<CEDate> MDR_DATE_COMPARATOR_INTERVAL_START_NULLS_FIRST = new Comparator<CEDate>() {
        @Override
        public int compare(@Nullable CEDate left, @Nullable CEDate right) {
            if (left == null) {
                return right == null ? 0 : -1;
            }
            if (right == null) {
                return 1;
            }
            return MDR_DATE_COMPARATOR_INTERVAL_START_NO_NULLS.compare(left, right);
        }
    };

    public static final Comparator<CEDate> MDR_DATE_COMPARATOR_INTERVAL_START_NULLS_LAST = new Comparator<CEDate>() {
        @Override
        public int compare(@Nullable CEDate left, @Nullable CEDate right) {
            if (left == null) {
                return right == null ? 0 : 1;
            }
            if (right == null) {
                return -1;
            }
            return MDR_DATE_COMPARATOR_INTERVAL_START_NO_NULLS.compare(left, right);
        }
    };

    public static final Comparator<CEDate> MDR_DATE_COMPARATOR_INTERVAL_END_NULLS_FIRST = new Comparator<CEDate>() {
        @Override
        public int compare(@Nullable CEDate left, @Nullable CEDate right) {
            if (left == null) {
                return right == null ? 0 : -1;
            }
            if (right == null) {
                return 1;
            }
            return MDR_DATE_COMPARATOR_INTERVAL_END_NO_NULLS.compare(left, right);
        }
    };

    public static final Comparator<CEDate> MDR_DATE_COMPARATOR_INTERVAL_END_NULLS_LAST = new Comparator<CEDate>() {
        @Override
        public int compare(@Nullable CEDate left, @Nullable CEDate right) {
            if (left == null) {
                return right == null ? 0 : 1;
            }
            if (right == null) {
                return -1;
            }
            return MDR_DATE_COMPARATOR_INTERVAL_END_NO_NULLS.compare(left, right);
        }
    };

}
