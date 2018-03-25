package export.cli;

import com.google.inject.Guice;
import com.google.inject.Injector;
import export.ExportResult;
import export.Exporter;
import export.inject.ExporterModule;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command line tool to extract export files out of Cassandra.
 * Created by shengli on 12/22/15.
 */
public class ExportCLI {
    private static final Logger log = LoggerFactory.getLogger(ExportCLI.class);

        public static void main(String args[]) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    log.info("Shutting down logging");
                    LogManager.shutdown();
                } catch (Exception e) {
                    log.error("Unable to cleanly shut down logging", e);
                }
            }
        });

        Exporter exporter = setUpExporter(args);

            try {
                exporter.processConfiguration();
            } catch (Exception e) {
                log.error("Error while processing export configuration file", e);
            }

            try {
            exporter.doEverything();
        } catch (Exception e) {
            log.error("Error while exporting", e);
        }
        ExportResult exportResult = exporter.exportResult();
        System.exit(exportResult == null ? -2 : exportResult.isSucceeded() ? 0 : -1);
    }

    private static Exporter setUpExporter(String[] args) {
        ExporterModule exporterModule = new ExporterModule();
        exporterModule.configureFromCommandLine(args);
        Injector injector = Guice.createInjector(exporterModule);
        return injector.getInstance(Exporter.class);
    }
}
