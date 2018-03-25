package cp.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public final class Converters {
    private Converters() {}

    /**
     * Helper to convert a given object to boolean value. Does extra checks for strings like yes/no, y/n, etc.
     *
     * @param value Object to be converted to boolean.
     * @return Appropriate boolean value.
     */
    public static boolean toBoolean(@Nullable Object value) {
        if (value == null)
            return false;

        return boolStringCache.getUnchecked(ValueCache.intern(value.toString()));
    }

    static final String[] FALSE_PHRASES = {"f", "false", "no", "n", "0"};
    static final String[] TRUE_PHRASES = {"t", "true", "yes", "y", "1"};

    static {
        Arrays.sort(FALSE_PHRASES, String.CASE_INSENSITIVE_ORDER);
        Arrays.sort(TRUE_PHRASES, String.CASE_INSENSITIVE_ORDER);
    }

    private static final LoadingCache<String, Boolean> boolStringCache = CacheBuilder.newBuilder()
       .concurrencyLevel(8)
       .expireAfterAccess(60, TimeUnit.MINUTES)
       .build(new CacheLoader<String, Boolean>() {
           @Override
           public Boolean load(String name)
              throws Exception {
               if (Arrays.binarySearch(FALSE_PHRASES, name, String.CASE_INSENSITIVE_ORDER) > -1)
                   return false;

               if (Arrays.binarySearch(TRUE_PHRASES, name, String.CASE_INSENSITIVE_ORDER) > -1)
                   return true;

               return false;
           }
       });
}
