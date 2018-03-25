package cp.util;

import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Mainly an insulating layer against Guava version changes.
 * But also a few handy utility methods.
 **/
public class ObjectUtil {

    private ObjectUtil() {}

    public static MoreObjects.ToStringHelper toStringHelper(Object object) {
        return MoreObjects.toStringHelper(object);
    }

    public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
        return MoreObjects.firstNonNull(first, second);
    }

    /**
     * Turns object into a living breathing predicate.
     * @param subject The subject of the predicate
     * @return a predicator.
     */
    public static Predicator is(@Nullable Object subject) {
        return new Predicator(subject);
    }


    /**
     * A handy, null-friendly class for writing syntactic predicates in Java
     */
    public static class Predicator {

        final Object subject;

        public Predicator(@Nullable Object subject) {
            this.subject = subject;
        }

        /**
         * Determines membership in a varargs array of objects.
         * @param objects The array of objects against which to check the subject
         * @return  <tt>true</tt> if any are equal, <tt>false</tt> otherwise.
         */
        public boolean in(Object ...objects) {
            for (Object candidate : objects) {
                if (Objects.equals(subject, candidate)) {
                    return true;
                }
            }
            return false;
        }
    }
}
