package cp.services;

import cp.config.ConfigurationService;
import cp.config.ConstantConfigurationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * this is a wrapper class for LiveLookUpServiceImpl in the service module
 * Created by shengli on 3/16/16.
 */
@Service("SpringLookupService")
public class SpringLookupService {
    @Autowired
    private CassandraConnectionService cassandraConnectionService;

    private LiveLookupServiceSupplier liveLookupServiceSupplier;

    public SpringLookupService(){
        ConfigurationService configurationService = new ConstantConfigurationServiceImpl();
        liveLookupServiceSupplier = new LiveLookupServiceSupplier(cassandraConnectionService.getCassandraSessionManagerSupplier(), configurationService);
    }

    public LookupService getLookupService(){
        return this.liveLookupServiceSupplier.get();
    }

}
