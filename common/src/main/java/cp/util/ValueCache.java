package cp.util;

import com.google.common.base.Ascii;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@CheckReturnValue
public class ValueCache {
    private ValueCache() {
    }

    /**
     * TODO: Evaluate discarding String or potentially all of these interners when upgrading to Java 8
     * (See https://code.google.com/p/guava-libraries/issues/detail?id=399 for more information
     * about context of doing it this way and which specific jvm platform versions the below
     * addresses)
     */

    private static final Interner<String> weakStringCache = Interners.newWeakInterner();

    public static String intern(@Nullable String src) {
        if (src == null)
            return null;

        return weakStringCache.intern(src);
    }

    private static final Interner<String> strongStringCache = Interners.newStrongInterner();

    public static String internStrong(@Nullable String src) {
        if (src == null)
            return null;

        return strongStringCache.intern(src);
    }

    private static final LoadingCache<String, String> upperStrongInternMap = CacheBuilder.newBuilder()
       .build(new CacheLoader<String, String>() {
           public String load(@Nonnull String key) {
               return internStrong(Ascii.toUpperCase(key));
           }
       });

    public static String internUpperStrong(@Nullable String src) {
        if (src == null)
            return null;

        return upperStrongInternMap.getUnchecked(src);
    }

    private static final Interner<Integer> intCache = Interners.newWeakInterner();

    public static Integer intern(@Nullable Integer val) {
        if (val == null)
            return null;

        return intCache.intern(val);
    }

    public static Integer intern(int val) {
        return intCache.intern(val);
    }

    private static final Interner<Integer> strongIntCache = Interners.newStrongInterner();

    public static Integer internStrong(@Nullable Integer val) {
        if (val == null)
            return null;

        return strongIntCache.intern(val);
    }

    public static Integer internStrong(int val) {
        return strongIntCache.intern(val);
    }

    private static final Interner<Long> longCache = Interners.newWeakInterner();

    public static Long intern(@Nullable Long val) {
        if (val == null)
            return null;

        return longCache.intern(val);
    }

    public static Long intern(long val) {
        return longCache.intern(val);
    }
}
