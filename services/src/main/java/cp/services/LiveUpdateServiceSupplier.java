package cp.services;


import cp.config.ConfigurationService;
import cp.connect.CassandraSessionManagerSupplier;

public class LiveUpdateServiceSupplier implements UpdateServiceSupplier {

    private final ConfigurationService configService;

    private final CassandraSessionManagerSupplier sessionManagerSupplier;

    public LiveUpdateServiceSupplier(ConfigurationService configService, CassandraSessionManagerSupplier sessionManagerSupplier) {
        this.configService = configService;
        this.sessionManagerSupplier = sessionManagerSupplier;
    }

    @Override
    public UpdateService get() {
        return new UpdateServiceImpl(configService, sessionManagerSupplier.get());
    }
}
