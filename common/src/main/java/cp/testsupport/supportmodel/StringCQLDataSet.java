package cp.testsupport.supportmodel;

import org.cassandraunit.dataset.CQLDataSet;
import org.cassandraunit.dataset.cql.AbstractCQLDataSet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Implementation of CassandraUnit CQLDataSet to read a CQL script from a string in memory.
 *
 * Created by shengli on 10/14/15.
 */
public class StringCQLDataSet extends AbstractCQLDataSet implements CQLDataSet {

    private String cqlScript;

    public StringCQLDataSet(String cqlScript) {
        super("", true, true, null);
        this.cqlScript = cqlScript;
    }

    public StringCQLDataSet(String cqlScript, boolean keyspaceCreation, boolean keyspaceDeletion) {
        super("", keyspaceCreation, keyspaceDeletion, null);
        this.cqlScript = cqlScript;
    }

    public StringCQLDataSet(String cqlScript, String keyspaceName) {
        super("", true, true, keyspaceName);
        this.cqlScript = cqlScript;
    }

    public StringCQLDataSet(String cqlScript, boolean keyspaceCreation) {
        super("", true, true, null);
        this.cqlScript = cqlScript;
    }

    public StringCQLDataSet(String cqlScript, boolean keyspaceCreation, boolean keyspaceDeletion, String keyspaceName) {
        super("", keyspaceCreation, keyspaceDeletion, keyspaceName);
        this.cqlScript = cqlScript;
    }


    /**
     * @param dataSetLocation is ignored
     * @return Dummy stream if initializing (which actually happens), or real stream if cqlScript present
     */
    @Override
    protected InputStream getInputDataSetLocation(String dataSetLocation) {
        if (cqlScript == null) {
            return new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("No cqlScript string present");
                }
            };
        }
        return new ByteArrayInputStream(cqlScript.getBytes(Charset.forName("UTF-8")));
    }
}
