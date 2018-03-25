package cp.services;

/**
 * Mocked implementation of the LookupServiceSupplier, for unit testing
 *
 * Created by shengli on 9/16/15.
 */
public class MockLookupServiceSupplier implements LookupServiceSupplier {

    private static final long serialVersionUID = -5084795035920106754L;
    private transient MockLookupService lookupService;

    public MockLookupServiceSupplier() {
    }

    @Override
    public LookupService get() {
        if (lookupService == null) {
            lookupService = new MockLookupService();
        }
        return lookupService;
    }
}
