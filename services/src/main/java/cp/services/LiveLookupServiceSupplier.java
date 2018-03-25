package cp.services;

import cp.config.ConfigurationService;
import cp.connect.CassandraSessionManagerSupplier;

public class LiveLookupServiceSupplier implements LookupServiceSupplier {

    private final CassandraSessionManagerSupplier sessionManagerSupplier;
    private final ConfigurationService configService;

    public LiveLookupServiceSupplier(CassandraSessionManagerSupplier sessionManagerSupplier, ConfigurationService configService) {
        this.sessionManagerSupplier = sessionManagerSupplier;
        this.configService = configService;
    }

    @Override
    public LookupService get() {
        return new LookupServiceImpl(sessionManagerSupplier.get(), configService);
    }
}
