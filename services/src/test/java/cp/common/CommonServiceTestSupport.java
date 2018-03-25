package cp.common;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import cp.connect.CassandraSessionManager;
import cp.connect.CassandraSessionManagerSupplier;
import cp.config.ConfigurationService;
import cp.config.ConstantConfigurationServiceImpl;
import cp.config.DelegatingConfigurationServiceImpl;
import cp.config.PropertiesConfigurationServiceImpl;
import cp.services.*;
import cp.testsupport.MockCassandraSessionManagerSupplier;

import java.util.Properties;

import static cp.config.ConfigurationKeys.*;

/**
 * Created by shengli on 12/22/15.
 */
public class CommonServiceTestSupport {
    private static ConfigurationService configurationService;

    public static final Supplier<Properties> CONFIG_PROPERTIES_SUPPLIER = Suppliers.memoize(new Supplier<Properties>() {
        @Override
        public Properties get() {
            Properties dynamicProperties = new Properties();
            dynamicProperties.setProperty(LIMIT_KEY, "10000");
            dynamicProperties.setProperty(BATCH_SIZE_KEY, "10000");
            dynamicProperties.setProperty(MAIN_KEYSPACE_KEY, "main");
            dynamicProperties.setProperty(REFERENCE_KEYSPACE_KEY, "reference");
            dynamicProperties.setProperty(USE_BATCH_DELETE_KEY, "true");
            dynamicProperties.setProperty(SPARK_MASTER_KEY, "local");
            return dynamicProperties;
        }
    });

    private static final Supplier<ConfigurationService> CONFIG_SERVICE_SUPPLIER = Suppliers.memoize(new Supplier<ConfigurationService>() {
        @Override
        public ConfigurationService get() {
            if (configurationService == null) {
                ConstantConfigurationServiceImpl constantService = new ConstantConfigurationServiceImpl();
                PropertiesConfigurationServiceImpl propsService = new PropertiesConfigurationServiceImpl(CONFIG_PROPERTIES_SUPPLIER.get());
                configurationService = DelegatingConfigurationServiceImpl.builder()
                        .add(propsService)
                        .add(constantService)
                        .build();
            }
            return configurationService;
        }
    }) ;

    public static ConfigurationService getConfigService() {
        return CONFIG_SERVICE_SUPPLIER.get();
    }

    public static final CassandraSessionManagerSupplier SESSION_MANAGER_SUPPLIER = new MockCassandraSessionManagerSupplier();

    public static CassandraSessionManager getSessionManager() {
        return SESSION_MANAGER_SUPPLIER.get();
    }

    public static final LookupServiceSupplier LOOKUP_SERVICE_SUPPLIER = new MockLookupServiceSupplier();

    public static LookupService getLookupService() {
        return LOOKUP_SERVICE_SUPPLIER.get();
    }

    public static final UpdateServiceSupplier UPDATE_SERVICE_SUPPLIER = new MockUpdateServiceSupplier();

    public static UpdateService getUpdateService() {
        return UPDATE_SERVICE_SUPPLIER.get();
    }

}
