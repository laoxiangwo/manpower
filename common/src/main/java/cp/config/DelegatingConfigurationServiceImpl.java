package cp.config;

import java.util.LinkedList;

/**
 * Checks a series of configuration service implementations for configured values,
 * in order
 */
public class DelegatingConfigurationServiceImpl extends AbstractConfigurationServiceImpl {

    private static final long serialVersionUID = 7403989270883216809L;

    private final ConfigurationService delegates[];

    public DelegatingConfigurationServiceImpl(ConfigurationService delegates[]) {
        this.delegates = delegates;
    }

    @Override
    public String getValue(String key) {
        for (ConfigurationService delegate : delegates) {
            String fetched = delegate.getValue(key);
            if (fetched != null) {
                return fetched;
            }
        }
        return null;
    }

    public static DCSIBuilder builder() {
        return new DCSIBuilder();
    }

    public static class DCSIBuilder {
        private LinkedList<ConfigurationService> services = new LinkedList<>();

        public DCSIBuilder() {}

        public DCSIBuilder add(ConfigurationService service) {
            services.add(service);
            return this;
        }

        public DelegatingConfigurationServiceImpl build() {
            return new DelegatingConfigurationServiceImpl(services.toArray(new ConfigurationService[services.size()]));
        }
    }
}
