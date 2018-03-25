package cp.services;

/**
 * Mocked implementation of the UpdateServiceSupplier, for unit testing
 *
 * Created by shengli on 9/16/15.
 */
public class MockUpdateServiceSupplier implements UpdateServiceSupplier {
    private static final long serialVersionUID = 6940714344963650748L;
    private transient MockUpdateService updateService;

    public MockUpdateServiceSupplier() {
    }

    @Override
    public UpdateService get() {
        if (updateService == null) {
            updateService = new MockUpdateService();
        }
        return updateService;
    }

}
