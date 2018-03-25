package cp.config;

import java.io.Serializable;

/**
 * Interface encapsulating all
 */
public interface ConfigurationService extends Serializable {

    String getValue(String key);

    Integer getIntegerValue(String key);

    Long getLongValue(String key);

    Double getDoubleValue(String key);

    Boolean getBooleanValue(String key);

    String getValue(String key, String defaultValue);

    Integer getIntegerValue(String key, Integer defaultValue);

    Long getLongValue(String key, Long defaultValue);

    Double getDoubleValue(String key, Double defaultValue);

    Boolean getBooleanValue(String key, Boolean defaultValue);

}
