package cp.metadata;

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * Dynamically create configuration info for MyBatis SqlSessionFactory building
 */
public class MetadataSqlSessionBuilder {

    public static SqlSessionFactory createFor(DataSource dataSource, String environmentName) {
        return factoryCache.getUnchecked(new DSENCacheKey(dataSource, environmentName));
    }

    private static final LoadingCache<DSENCacheKey, SqlSessionFactory> factoryCache = CacheBuilder.newBuilder()
            .weakKeys()
            .weakValues()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<DSENCacheKey, SqlSessionFactory>() {
                @Override
                public SqlSessionFactory load(DSENCacheKey cacheKey)
                        throws Exception {
                    return build(cacheKey.dataSource, cacheKey.environmentName);
                }
            });

    private static Configuration addMappers(Configuration conf) {
        conf.addMapper(MetadataMapper.class);
        // additional mapper configurations go here

        return conf;
    }

    private static Configuration registerAliases(Configuration conf) {
        TypeAliasRegistry aliasRegistry = conf.getTypeAliasRegistry();

        //aliasRegistry.registerAlias(County.class);
        // additional type aliases go here

        return conf;
    }

    private static Configuration registerTypeHandlers(Configuration conf) {
        TypeHandlerRegistry typeHandlerRegistry = conf.getTypeHandlerRegistry();

        // additional type handlers go here

        return conf;
    }

    private static SqlSessionFactory build(DataSource dataSource, String envName) {
        Environment env = new Environment.Builder(envName)
                .dataSource(dataSource)
                .transactionFactory(new JdbcTransactionFactory())
                .build();

        Configuration conf = new Configuration(env);

        registerAliases(conf);
        registerTypeHandlers(conf);
        addMappers(conf);

        // general session factory configuration
        conf.setDefaultExecutorType(ExecutorType.REUSE);
        conf.setCacheEnabled(true);
        conf.setUseGeneratedKeys(true);

        conf.setProxyFactory(new JavassistProxyFactory());
        conf.setLazyLoadingEnabled(true);
        conf.setAggressiveLazyLoading(false);

        return new SqlSessionFactoryBuilder().build(conf);
    }

    private static class DSENCacheKey {
        private final DataSource dataSource;
        private final String environmentName;

        private DSENCacheKey(DataSource dataSource, String environmentName) {
            this.dataSource = dataSource;
            this.environmentName = environmentName;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public String getEnvironmentName() {
            return environmentName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DSENCacheKey that = (DSENCacheKey) o;
            return Objects.equal(dataSource, that.dataSource) &&
                    Objects.equal(environmentName, that.environmentName);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(dataSource, environmentName);
        }
    }
}
