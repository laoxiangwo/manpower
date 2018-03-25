package cp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class ExecutorShutdownHook implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ExecutorShutdownHook.class);

    private final ExecutorService pool;

    public ExecutorShutdownHook(ExecutorService pool) {
        this.pool = pool;
    }

    public static <T extends ExecutorService> T addShutdownHook(ExecutorService pool) {
        Runtime.getRuntime().addShutdownHook(new Thread(new ExecutorShutdownHook(pool)));
        return (T) pool;
    }

    @Override
    public void run() {
        try {
            pool.shutdownNow();
        } catch (Throwable t) {
            log.error("Error shutting down thread pool..", t);
        }
    }
}
