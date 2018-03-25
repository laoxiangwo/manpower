package cp.util;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;

public class JavaTimeUtil {

    /**
     * Given a possibly-null date, return a possibly-null corresponding LocalDate
     *
     * @param date A Date object or <tt>null</tt>
     * @return The corresponding LocalDate object, or <tt>null</tt>
     */
    @Nullable
    public static LocalDate toLocalDate(@Nullable Date date) {
       return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Given a possibly-null date, return a possibly-null corresponding LocalDateTime
     *
     * @param date A Date object or <tt>null</tt>
     * @return The corresponding LocalDateTime object, or <tt>null</tt>
     */
    @Nullable
    public static LocalDateTime toLocalDateTime(@Nullable Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Convert a LocalDate to a plain ol java.util.Date
     *
     * @param localDate A LocalDate object or <tt>null</tt>
     * @return The corresponding Date object, or <tt>null</tt>
     */
    @Nullable
    public static Date toDate(@Nullable LocalDate localDate) {
       return localDate == null ? null : Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convert a LocalDateTime to a plain ol java.util.Date
     * @param localDateTime A LocalDateTime object or <tt>null</tt>
     * @return The corresponding Date object, or <tt>null</tt>
     */
    @Nullable
    public static Date toDate(@Nullable LocalDateTime localDateTime) {
        return localDateTime == null ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Given a possibly null CEDate object, return the possibly
     * null string representation of that object
     * @param CEDate An CEDate object, or <tt>null</tt>
     * @return The String representation, or <tt>null</tt>
     */
    public static String mdrDateToString(@Nullable CEDate CEDate) {
        return CEDate == null ? null : CEDate.toString();
    }

    public static final Comparator<LocalDate> LOCAL_DATE_COMPARATOR_NULLS_LAST = (left, right) -> {
        if (left == null) {
            return right == null ? 0 : 1;
        }
        if (right == null) {
            return -1;
        }
        return left.isBefore(right) ? -1 : left.isAfter(right) ? 1 : 0;
    };

    public static final Comparator<LocalDate> LOCAL_DATE_COMPARATOR_NULLS_FIRST = (left, right) -> {
        if (left == null) {
            return right == null ? 0 : -1;
        }
        if (right == null) {
            return 1;
        }
        return left.isBefore(right) ? -1 : left.isAfter(right) ? 1 : 0;
    };

    public static final Comparator<LocalDateTime> LOCAL_DATE_TIME_COMPARATOR_NULLS_LAST = (left, right) -> {
        if (left == null) {
            return right == null ? 0 : 1;
        }
        if (right == null) {
            return -1;
        }
        return left.isBefore(right) ? -1 : left.isAfter(right) ? 1 : 0;
    };

    public static final Comparator<LocalDateTime> LOCAL_DATE_TIME_COMPARATOR_NULLS_FIRST = (left, right) -> {
        if (left == null) {
            return right == null ? 0 : -1;
        }
        if (right == null) {
            return 1;
        }
        return left.isBefore(right) ? -1 : left.isAfter(right) ? 1 : 0;
    };

    public static final boolean isLeftDateNewer(LocalDate left, CEDate right) {
        if(left == null)
            return false;
        return isLeftDateNewer(cp.util.CEDate.parse(left.toString()), right);
    }

    public static final boolean isLeftDateNewer(CEDate left, LocalDate right) {
        if(right == null)
            return true;
        return isLeftDateNewer(left, cp.util.CEDate.parse(right.toString()));
    }

    public static final boolean isLeftDateNewer(CEDate left, CEDate right) {
        return left == null ? false : (right == null ? true : left.isAfter(right));
    }

    /**
     * Nice null handling toString for LocalDateTime objects
     * @param from the LocalDateTime to render
     * @return the string version, or <tt>null</tt> if the input is <tt>null</tt>
     */
    @Nullable
    public static String toString(@Nullable LocalDateTime from) {
        return from == null ? null : from.toString();
    }

    /**
     * Nice null handling toString for LocalDate objects
     * @param from the LocalDate to render
     * @return the string version, or <tt>null</tt> if the input is <tt>null</tt>
     */
    @Nullable
    public static String toString(@Nullable LocalDate from) {
        return from == null ? null : from.toString();
    }
}
