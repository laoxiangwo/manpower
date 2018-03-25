package export.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import cp.config.ConfigurationService;
import cp.config.ConstantConfigurationServiceImpl;
import cp.config.DelegatingConfigurationServiceImpl;
import cp.config.PropertiesConfigurationServiceImpl;
import cp.connect.CassandraSessionManager;
import cp.connect.LiveCassandraSessionManagerImpl;
import cp.util.CommandLinePropertiesUtil;
import export.Exporter;

import java.util.Properties;

import static cp.config.ConfigurationKeys.*;

/**
 * Guice Module for the Exporter
 *
 * Created by shengli on 12/22/15.
 */
public class ExporterModule extends AbstractModule {

    private String commandLineArguments[];

    @Override
    protected void configure() {
        bind(Exporter.class);
    }

    public void configureFromCommandLine(String[] args) {
        commandLineArguments = args;
    }

    @Provides
    public ConfigurationService buildConfigurationService() {
        Properties commandLineProperties = CommandLinePropertiesUtil.setOptionsAsProperties(commandLineArguments);

        // add in some syntactic sugar to allow shorter keyspace option on the command line
        if (commandLineProperties.containsKey(EXPORT_KEYSPACE_KEY)) {
            commandLineProperties.setProperty(MAIN_KEYSPACE_KEY, commandLineProperties.getProperty(EXPORT_KEYSPACE_KEY));
        }

        PropertiesConfigurationServiceImpl propertiesConfigService = new PropertiesConfigurationServiceImpl(commandLineProperties);
        ConstantConfigurationServiceImpl constantConfigService = new ConstantConfigurationServiceImpl();
        return DelegatingConfigurationServiceImpl.builder().add(propertiesConfigService)
                .add(constantConfigService)
                .build();
    }

    @Provides
    public CassandraSessionManager buildCassandraSessionManager(ConfigurationService configService) {
        return new LiveCassandraSessionManagerImpl(configService);
    }

}
