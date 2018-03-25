package cp.util;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class StatementCache {
    private static final Logger log = LoggerFactory.getLogger(StatementCache.class);

    private final Session session;

    public StatementCache(Session session) {
        this.session = session;
    }

    public PreparedStatement cachedPrepare(String query) {
        return statementCache.getUnchecked(query);
    }

    private final LoadingCache<String, PreparedStatement> statementCache = CacheBuilder.newBuilder()
       .expireAfterAccess(1, TimeUnit.HOURS)
       .build(new CacheLoader<String, PreparedStatement>() {
           public PreparedStatement load(@Nonnull String key) {
               log.debug(String.format("Cachable PreparedStatement in Keyspace [%s]", session.getLoggedKeyspace()));

               return session.prepare(key);
           }
       });
}
