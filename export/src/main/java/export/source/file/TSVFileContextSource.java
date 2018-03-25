package export.source.file;

import com.google.common.base.Joiner;
import export.model.ExportSpecification;
import export.model.ExpressionContext;
import export.model.generic.MapExpressionContext;
import export.source.ContextSource;
import export.source.ContextSourceException;
import export.util.TSVUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Generates expression contexts from TSV lines
 *
 * Created by shengli on 12/28/15.
 */
public class TSVFileContextSource implements ContextSource {
    private static final Logger log = LoggerFactory.getLogger(TSVFileContextSource.class);

    private LineNumberReader reader;
    private List<String> headers;

    public TSVFileContextSource(Reader in) {
        if (in instanceof LineNumberReader) {
            reader = (LineNumberReader) in;
        } else {
            reader = new LineNumberReader(in);
        }
        String headerLine;
        try {
            headerLine = reader.readLine();
        } catch (IOException e) {
            throw new ContextSourceException("Unable to read", e);
        }
        if (headerLine == null) {
            throw new ContextSourceException("File is empty");
        }
        headers = TSVUtil.parseTSVLine(headerLine);
        if (log.isDebugEnabled()) {
            log.debug("File headers: {}", Joiner.on(", ").join(headers));
        }

    }

    @Override
    public Iterable<ExpressionContext> get() {
        return () -> new Iterator<ExpressionContext>() {
            private String line = null;
            @Override
            public boolean hasNext() {
                if (line != null) {
                    return true;
                }
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    throw new ContextSourceException("Unable to read", e);
                }
                return line != null;
            }

            @Override
            public ExpressionContext next() {

                if (line == null) {
                    try {
                        line = reader.readLine();
                    } catch (IOException e) {
                        throw new ContextSourceException("Unable to read", e);
                    }
                    if (line == null) {
                        throw new ContextSourceException("End of input reached but requesting next context");
                    }
                }

                List<String> lineValues = TSVUtil.parseTSVLine(line);
                if (lineValues.size() != headers.size()) {
                    throw new ContextSourceException(String.format("Line has %d entries, differs from header with %d entries",
                            lineValues.size(), headers.size()));
                }
                Map<String, String> map = new HashMap<>(headers.size());
                Iterator<String> valueIter = lineValues.iterator();
                for (String header : headers) {
                    map.put(header, valueIter.next());
                }
                line = null;
                return new MapExpressionContext(map);
            }
        };
    }

    @Override
    public void setExportSpecification(ExportSpecification exportSpecification) {
    }


}
