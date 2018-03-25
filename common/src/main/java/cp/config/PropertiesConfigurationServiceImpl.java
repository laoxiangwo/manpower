package cp.config;

import java.util.Properties;

/**
 * Property object based config service implementation
 */
public class PropertiesConfigurationServiceImpl extends AbstractConfigurationServiceImpl {

    private static final long serialVersionUID = -2401229630572644055L;

    private Properties properties;

    public PropertiesConfigurationServiceImpl(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getValue(String key) {
        return properties.getProperty(key);
    }

    public static PropertiesConfigurationServiceImpl fromProperties(Properties properties) {
        return new PropertiesConfigurationServiceImpl(properties);
    }

}
