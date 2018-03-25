package cp.services;

import cp.config.ConfigurationService;
import cp.config.ConstantConfigurationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * this is a wrapper class for LiveUpdateServiceImpl in the service module
 * Created by shengli on 3/16/16.
 */
@Service("SpringUpdateService")
public class SpringUpdateService {
    @Autowired
    private CassandraConnectionService cassandraConnectionService;

    private LiveUpdateServiceSupplier liveLookupServiceSupplier;

    public SpringUpdateService(){
        ConfigurationService configurationService = new ConstantConfigurationServiceImpl();
        liveLookupServiceSupplier = new LiveUpdateServiceSupplier(configurationService, cassandraConnectionService.getCassandraSessionManagerSupplier());
    }

    public UpdateService getUpdateService(){
        return this.liveLookupServiceSupplier.get();
    }
}
