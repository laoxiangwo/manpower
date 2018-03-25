package cp.config;

import cp.exceptions.ConfigurationException;

import java.io.Serializable;

/**
 * Groups all the useful type conversion and defaulting methods in one place
 */
public abstract class AbstractConfigurationServiceImpl implements ConfigurationService, Serializable {

    private static final long serialVersionUID = -6684983762032564538L;

    @Override
    public Integer getIntegerValue(String key) {
        String fetched = getValue(key);
        try {
            return fetched == null ? null : Integer.valueOf(fetched);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(
               String.format("Config value for key %s cannot be converted to Integer: %s", key, fetched));
        }
    }

    @Override
    public Long getLongValue(String key) {
        String fetched = getValue(key);
        try {
            return fetched == null ? null : Long.valueOf(fetched);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(
               String.format("Config value for key %s cannot be converted to Long: %s", key, fetched));
        }
    }

    @Override
    public Double getDoubleValue(String key) {
        String fetched = getValue(key);
        try {
            return fetched == null ? null : Double.valueOf(fetched);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(
               String.format("Config value for key %s cannot be converted to Double: %s", key, fetched));
        }
    }

    @Override
    public Boolean getBooleanValue(String key) {
        String fetched = getValue(key);
        try {
            return fetched == null ? null : Boolean.valueOf(fetched);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(
               String.format("Config value for key %s cannot be converted to Boolean: %s", key, fetched));
        }
    }

    @Override
    public Boolean getBooleanValue(String key, Boolean defaultValue) {
        String fetched = getValue(key);
        return fetched == null ? defaultValue : Boolean.valueOf(fetched);
    }

    @Override
    public Integer getIntegerValue(String key, Integer defaultValue) {
        String fetched = getValue(key);
        return fetched == null ? defaultValue : Integer.valueOf(fetched);
    }

    @Override
    public Long getLongValue(String key, Long defaultValue) {
        String fetched = getValue(key);
        return fetched == null ? defaultValue : Long.valueOf(fetched);
    }

    @Override
    public Double getDoubleValue(String key, Double defaultValue) {
        String fetched = getValue(key);
        return fetched == null ? defaultValue : Double.valueOf(fetched);
    }

    @Override
    public String getValue(String key, String defaultValue) {
        String fetched = getValue(key);
        return fetched == null ? defaultValue : fetched;
    }
}
