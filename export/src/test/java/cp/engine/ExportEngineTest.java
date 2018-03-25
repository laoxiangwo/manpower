package cp.engine;

import com.google.common.collect.Lists;
import export.engine.ExportEngine;
import export.model.ColumnExpression;
import export.model.ExportSpecification;
import export.model.generic.ConstantColumnExpression;
import export.model.generic.IdentityColumnExpression;
import export.model.python.PythonColumnExpression;
import export.model.python.PythonEngine;
import export.sink.file.TSVFileOutputSink;
import export.source.file.TSVFileContextSource;
import org.assertj.core.api.Assertions;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * Test the export engine
 * Created by shengli on 12/30/15.
 */
public class ExportEngineTest {
    private static final Logger log = LoggerFactory.getLogger(ExportEngineTest.class);

    @Test (enabled = false)
    public void testPerformExport() throws Exception {
        System.setProperty("python.import.site", "false");
        PythonEngine pythonEngine = new PythonEngine(new PythonInterpreter());
        ExportSpecification exportSpec = new ExportSpecification();
        List<String> inputColumns = Lists.newArrayList("DWID", "STATE", "FIRSTNAME", "LASTNAME", "AGE", "BIRTHDATE");
        exportSpec.setColumns(inputColumns);
        exportSpec.setKeyspace("no_keyspace");
        exportSpec.setTable("no_table");
        exportSpec.setAllowFiltering(false);
        exportSpec.setLimit(1000L);
        exportSpec.setPredicate("no predicate");
        exportSpec.setOmitHeader(false);

        List<ColumnExpression> outputExpressions = new ArrayList<>(6);
        outputExpressions.add(new ConstantColumnExpression("ALWAYS", "THERE", 1));
        outputExpressions.add(new IdentityColumnExpression("DWID", "DWID", 2));
        outputExpressions.add(new IdentityColumnExpression("STATE", "STATE", 3));
        outputExpressions.add(new IdentityColumnExpression("FIRST_NAME", "FIRSTNAME", 4));
        outputExpressions.add(new IdentityColumnExpression("LAST_NAME", "LASTNAME", 5));
        outputExpressions.add(new PythonColumnExpression("AGE", "AGE", 6, pythonEngine));
        outputExpressions.add(new PythonColumnExpression("AGE_SQUARED", "AGE * AGE", 7, pythonEngine));
        exportSpec.setOutputExpressions(outputExpressions);

        InputStreamReader contextReader = new InputStreamReader(ExportEngineTest.class.getResourceAsStream("/context-file.tsv"));
        TSVFileContextSource contextSource = new TSVFileContextSource(contextReader);
        StringWriter outputWriter = new StringWriter();
        TSVFileOutputSink outputTarget = new TSVFileOutputSink(outputWriter);
        ExportEngine exportEngine = new ExportEngine(exportSpec, contextSource, outputTarget);
        exportEngine.performExport();

        final String stringOutput = outputWriter.toString();
        log.debug("Engine output:\n{}", stringOutput);

        List<String> outputLines = new ArrayList<>(11);
        outputLines.addAll(Lists.transform(Lists.newArrayList(stringOutput.split("\n")), String::trim));
        assertThat(outputLines.size()).isEqualTo(11);

        List<String> expectedLines = Lists.newArrayList(
                "ALWAYS\tDWID\tSTATE\tFIRST_NAME\tLAST_NAME\tAGE\tAGE_SQUARED" ,
                "THERE\t529197999\tCA\tNAVE\tFERREIRA\t40\t1600",
                "THERE\t250460534\tCA\tNELLIE\tFERREIRA\t48\t2304",
                "THERE\t926016754\tCA\tNELSON\tFERREIRA\t23\t529",
                "THERE\t146751398\tCA\tNELSON\tFERREIRA\t58\t3364",
                "THERE\t916245354\tCA\tNICHOLAS\tFERREIRA\t21\t441",
                "THERE\t242015089\tCA\tNICHOLAS\tFERREIRA\t40\t1600",
                "THERE\t292249839\tCA\tNICKI\tFERREIRA\t30\t900",
                "THERE\t104309979\tCA\tNICOLE\tFERREIRA\t32\t1024",
                "THERE\t98149314\tCA\tNICOLE\tFERREIRA\t37\t1369",
                "THERE\t916177049\tCA\tNOELLE\tFERREIRA\t42\t1764"
        );

        Assertions.assertThat(outputLines).containsOnlyElementsOf(expectedLines);

    }

    @Test (enabled = false)
    public void testOmitOutputHeader() throws Exception {
        System.setProperty("python.import.site", "false");
        PythonEngine pythonEngine = new PythonEngine(new PythonInterpreter());
        ExportSpecification exportSpec = new ExportSpecification();
        List<String> inputColumns = Lists.newArrayList("DWID", "STATE", "FIRSTNAME", "LASTNAME", "AGE", "BIRTHDATE");
        exportSpec.setColumns(inputColumns);
        exportSpec.setKeyspace("no_keyspace");
        exportSpec.setTable("no_table");
        exportSpec.setAllowFiltering(false);
        exportSpec.setLimit(1000L);
        exportSpec.setPredicate("no predicate");
        exportSpec.setOmitHeader(true);

        List<ColumnExpression> outputExpressions = new ArrayList<>(6);
        outputExpressions.add(new ConstantColumnExpression("ALWAYS", "THERE", 1));
        outputExpressions.add(new IdentityColumnExpression("DWID", "DWID", 2));
        outputExpressions.add(new IdentityColumnExpression("STATE", "STATE", 3));
        outputExpressions.add(new IdentityColumnExpression("FIRST_NAME", "FIRSTNAME", 4));
        outputExpressions.add(new IdentityColumnExpression("LAST_NAME", "LASTNAME", 5));
        outputExpressions.add(new PythonColumnExpression("AGE", "AGE", 6, pythonEngine));
        outputExpressions.add(new PythonColumnExpression("AGE_SQUARED", "AGE * AGE", 7, pythonEngine));
        exportSpec.setOutputExpressions(outputExpressions);

        InputStreamReader contextReader = new InputStreamReader(ExportEngineTest.class.getResourceAsStream("/context-file.tsv"));
        TSVFileContextSource contextSource = new TSVFileContextSource(contextReader);
        StringWriter outputWriter = new StringWriter();
        TSVFileOutputSink outputTarget = new TSVFileOutputSink(outputWriter);
        ExportEngine exportEngine = new ExportEngine(exportSpec, contextSource, outputTarget);
        exportEngine.performExport();

        final String stringOutput = outputWriter.toString();
        log.debug("Engine output:\n{}", stringOutput);

        List<String> outputLines = new ArrayList<>(10);
        outputLines.addAll(Lists.transform(Lists.newArrayList(stringOutput.split("\n")), String::trim));
        assertThat(outputLines.size()).isEqualTo(10);

        List<String> expectedLines = Lists.newArrayList(
           "THERE\t529197999\tCA\tNAVE\tFERREIRA\t40\t1600",
           "THERE\t250460534\tCA\tNELLIE\tFERREIRA\t48\t2304",
           "THERE\t926016754\tCA\tNELSON\tFERREIRA\t23\t529",
           "THERE\t146751398\tCA\tNELSON\tFERREIRA\t58\t3364",
           "THERE\t916245354\tCA\tNICHOLAS\tFERREIRA\t21\t441",
           "THERE\t242015089\tCA\tNICHOLAS\tFERREIRA\t40\t1600",
           "THERE\t292249839\tCA\tNICKI\tFERREIRA\t30\t900",
           "THERE\t104309979\tCA\tNICOLE\tFERREIRA\t32\t1024",
           "THERE\t98149314\tCA\tNICOLE\tFERREIRA\t37\t1369",
           "THERE\t916177049\tCA\tNOELLE\tFERREIRA\t42\t1764"
        );

        Assertions.assertThat(outputLines).containsOnlyElementsOf(expectedLines);
    }
}