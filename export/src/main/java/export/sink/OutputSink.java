package export.sink;

import java.util.List;

/**
 * Interface definition of a target for output of the export engine.
 *
 * Created by shengli on 12/29/15.
 */
public interface OutputSink {

    /**
     * Write out the headers for the export values
     *
     * @param headers the list of headers
     */
    void writeHeaders(List<String> headers);

    /**
     * Write out one record's worth of export values
     * @param values the values to write
     */
    void writeValues(List<String> values);

    /**
     * Return the number of lines written.
     *
     * @return A count of the lines written
     */
    int lineCount();


}
