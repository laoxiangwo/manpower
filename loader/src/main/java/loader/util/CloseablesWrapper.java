package loader.util;

import com.google.common.io.Closeables;

import java.io.Closeable;
import java.io.IOException;

/**
 * Helpful  wrapper around Google's volatile Closeables class, to insulate us from recent volatility.
 */
public class CloseablesWrapper {

    /**
     * Helpful wrapper around Google's volatile closeables closing method.
     */
    public static void closeQuietly(Closeable closeable)  {
        try {
            Closeables.close(closeable, true);
        } catch (IOException e) {
            // don't care
        }
    }

}
