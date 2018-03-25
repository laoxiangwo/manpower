package export;

import cp.config.ConfigurationKeys;
import cp.config.ConfigurationService;
import cp.connect.CassandraSessionManager;
import cp.exceptions.ConfigurationException;
import com.google.common.base.Strings;
import export.engine.ExportEngine;
import export.model.ExportException;
import export.model.ExportSpecification;
import export.model.python.PythonEngine;
import export.sink.OutputSink;
import export.sink.file.TSVFileOutputSink;
import export.source.cassandra.CassandraRowContextSupplier;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;

/**
 * Top-level business logic for the export process
 * Created by shengli on 12/22/15.
 */
public class Exporter {
    private static final Logger log = LoggerFactory.getLogger(Exporter.class);

    private ConfigurationService configService;

    private CassandraSessionManager sessionManager;

    private ExportSpecification exportSpec;

    private CassandraRowContextSupplier contextSource;

    private OutputSink outputSink;

    private PythonEngine pythonEngine;

    private ExportResult exportResult;

    String outputFilename;

    String configFileName;

    Long limit;

    private Writer outputWriter;

    @Inject
    public Exporter(ConfigurationService configService, CassandraSessionManager sessionManager) {
        this.configService = configService;
        this.sessionManager = sessionManager;
        pythonEngine = new PythonEngine(new PythonInterpreter());
    }

    public ExportResult exportResult() {
        return exportResult;
    }

    public void processConfiguration() {
        configFileName = configService.getValue(ConfigurationKeys.EXPORT_CONFIG_FILE_KEY);
        if (Strings.isNullOrEmpty(configFileName)) {
            throw new ConfigurationException(String.format("Missing --%s option", ConfigurationKeys.EXPORT_CONFIG_FILE_KEY));
        }

        outputFilename = configService.getValue(ConfigurationKeys.EXPORT_OUTPUT_FILE_KEY);
        if (Strings.isNullOrEmpty(outputFilename)) {
            throw new ConfigurationException(String.format("Missing --%s option", ConfigurationKeys.EXPORT_OUTPUT_FILE_KEY));
        }

        limit = configService.getLongValue(ConfigurationKeys.EXPORT_LIMIT_KEY);

        Reader configFileReader;
        try {
            configFileReader = new FileReader(configFileName);
        } catch (IOException e) {
            throw new ConfigurationException("Unable to read configuration file " + configFileName);
        }
        log.info("Opened configuration file {}", configFileName);

        try {
            exportSpec = PythonExportConfigurer.readExportSpec(configFileReader, configService, pythonEngine);
            exportSpec.setLimit(limit);
        } finally {
            try {
                configFileReader.close();
            } catch (IOException e) {
                log.error("Unable to close configuration file", e);
            }
        }
    }

    public void createContextSource() {
         CassandraRowContextSupplier crcs = new CassandraRowContextSupplier(sessionManager);
        crcs.setExportSpecification(exportSpec);
        crcs.start();
        contextSource = crcs;
        log.info("Opened cassandra context source");

    }

    public void createOutputTarget() {
        try {
            outputWriter = new FileWriter(outputFilename);
            outputSink = new TSVFileOutputSink(outputWriter);
        } catch (IOException e) {
            throw new ExportException("Unable to open output file " + outputFilename, e);
        }
        log.info("Opened output file {}", outputFilename);
    }

    public ExportResult runExport() {
        ExportEngine engine = new ExportEngine(exportSpec, contextSource, outputSink);
        return engine.performExport();
    }

    public void stop() {
        log.info("Stopping...");
        try {
            contextSource.stop();
        } catch (Exception e) {
            log.error("Error while stopping context source", e);
        }
        try {
            outputWriter.close();
        } catch (Exception e) {
            log.error("Error while closing output target", e);
        }
    }

    public void doEverything() {
        try {
            createContextSource();
            createOutputTarget();
            exportResult = runExport();
            log.info("Completed. Result: {}", exportResult);
        } catch (Exception e) {
            log.error("Error while executing", e);
            exportResult = new ExportResult().setSucceeded(false);
        } finally {
            stop();
        }

    }

}
