package cp.services;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Created to support unit testing on components which use a LookupService,
 * and also to support supplying instances to Spark jobs.
 *
 */
public interface LookupServiceSupplier extends Supplier<LookupService>, Serializable {
}
