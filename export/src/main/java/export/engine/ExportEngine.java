package export.engine;

import cp.util.DurationUtil;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import export.ExportResult;
import export.model.ColumnExpression;
import export.model.EvaluationException;
import export.model.ExportSpecification;
import export.model.ExpressionContext;
import export.sink.OutputSink;
import export.source.ContextSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Engine that makes exports go.
 * <p>
 * Created by shengli on 12/27/15.
 */
public class ExportEngine {
    private static final Logger log = LoggerFactory.getLogger(ExportEngine.class);

    private final ExportSpecification exportSpec;

    private final ContextSource contextSource;

    private final OutputSink outputSink;

    public ExportEngine(ExportSpecification exportSpec,
                        ContextSource contextSource,
                        OutputSink outputSink) {
        this.exportSpec = exportSpec;
        this.contextSource = contextSource;
        this.outputSink = outputSink;
    }

    public ExportResult performExport() {

        // write headers
        if (!exportSpec.getOmitHeader())
            outputSink.writeHeaders(Lists.transform(exportSpec.outputExpressions(), ColumnExpression::label));

        // prep
        List<String> values = new ArrayList<>(exportSpec.columns().size());

        // gather output rows
        long rowNumber = 0L;
        long bytes = 0L;
        Stopwatch timer = Stopwatch.createStarted();
        Iterable<ExpressionContext> expressionContextIterable = contextSource.get();
        long step = 10;
        long adjustAt = 100;
        for (ExpressionContext exprContext : expressionContextIterable) {
            rowNumber++;
            values.clear();
            log.trace("Row {} expression context: {}", rowNumber, exprContext);

            // gather output columns
            for (ColumnExpression colExpr : exportSpec.outputExpressions()) {
                Object evaluation = colExpr.evaluate(exprContext);
                try {
                    String output = (String) evaluation;
                    values.add(output);
                } catch (ClassCastException e) {
                    throw new EvaluationException(
                       String.format("Expected string value from expression %s, context %s, was %s instead",
                          colExpr, exprContext.name(), evaluation == null ? null : evaluation.getClass().getName()));
                }
            }


            // write line
            outputSink.writeValues(values);

            //bytes += line.length();

            // report
            if (rowNumber % step == 0) {
                log.info("Progress: Row {}, Elapsed: {}", rowNumber, DurationUtil.formatDuration(timer));
                if (rowNumber % adjustAt == 0) {
                    step = step * 10;
                    adjustAt = adjustAt * 10;
                }
            }

            if (rowNumber >= exportSpec.limit()) {
                log.info("Stopping at limit {}", exportSpec.limit());
                break;
            }
        }

        return new ExportResult().setSucceeded(true).setLineCount(rowNumber + 1).setDurationInMillis(timer.elapsed
           (TimeUnit.MILLISECONDS));
    }

}
