package cp.services;

import cp.config.ConfigurationService;
import cp.config.ConstantConfigurationServiceImpl;
import cp.connect.CassandraSessionManager;
import cp.connect.CassandraSessionManagerSupplier;
import cp.connect.LiveCassandraSessionManagerSupplier;
import org.springframework.stereotype.Service;

/**
 * this is a wrapper class for cassandraSessionManager in the common module
 * Created by shengli on 3/16/16.
 */
@Service("CassandraConnectionService")
public class CassandraConnectionService {
    private LiveCassandraSessionManagerSupplier liveCassandraSessionManagerSupplier;

    public CassandraConnectionService(){
        ConfigurationService configurationService = new ConstantConfigurationServiceImpl();
        liveCassandraSessionManagerSupplier = new LiveCassandraSessionManagerSupplier(configurationService);
    }

    public CassandraSessionManager getCassandraSessionManager(){
        return liveCassandraSessionManagerSupplier.get();
    }

    public CassandraSessionManagerSupplier getCassandraSessionManagerSupplier(){
        return this.liveCassandraSessionManagerSupplier;
    }
}
