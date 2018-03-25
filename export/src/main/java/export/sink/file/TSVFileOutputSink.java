package export.sink.file;


import export.sink.OutputSink;
import export.sink.OutputSinkException;
import export.util.TSVUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Direct export outputs to a TSV file.
 * <p>
 * Created by shengli on 12/29/15.
 */
public class TSVFileOutputSink implements OutputSink {

    private Writer writer;

    private int lineNumber;

    public TSVFileOutputSink(Writer out) {
        this.writer = out;
    }

    @Override
    public void writeHeaders(List<String> headers) {
        String line = TSVUtil.joinTSVLine(headers);
        try {
            writer.write(line);
        } catch (IOException e) {
            throw new OutputSinkException("Unable to write headers", e);
        }
        lineNumber++;
    }

    @Override
    public void writeValues(List<String> values) {
        String line = TSVUtil.joinTSVLine(values);
        try {
            if (lineNumber > 0)
                writer.write(System.lineSeparator());
            writer.write(line);
        } catch (IOException e) {
            throw new OutputSinkException("Unable to write values", e);
        }
        lineNumber++;
    }

    @Override
    public int lineCount() {
        return lineNumber;
    }


}
