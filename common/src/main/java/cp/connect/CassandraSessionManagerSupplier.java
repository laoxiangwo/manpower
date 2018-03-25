package cp.connect;

import java.io.Serializable;
import java.util.function.Supplier;

public interface CassandraSessionManagerSupplier extends Supplier<CassandraSessionManager>, Serializable {
}

