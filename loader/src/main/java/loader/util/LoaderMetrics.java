package loader.util;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class LoaderMetrics {

    private Long rowsInserted;
    private Long rowsRejected;
    private Long bytesProcessed;
    private Float bytesPerSecond;
    private Float rowsPerhour;

    private Stopwatch stopwatch;
    private boolean startReadingMetrics;

    public static LoaderMetrics startMetrics(){
        LoaderMetrics loaderMetrics = new LoaderMetrics();
        loaderMetrics.stopwatch = Stopwatch.createStarted();
        loaderMetrics.rowsInserted = new Long(0);
        loaderMetrics.rowsRejected = new Long(0);
        loaderMetrics.startReadingMetrics = true;
        return loaderMetrics;
    }

    public void rowInserted(Long bytes){
        bytesProcessed = bytesProcessed + bytes;
        rowsInserted++;
    }

    public void rowJected(Long bytes){
        bytesProcessed = bytesProcessed + bytes;
        rowsRejected++;
    }

    public void increaseRowsInsertedCount() throws MetricsException{
        if(startReadingMetrics){
            throw new MetricsException("Loader Metrics did not start. startMetrics() was not called.");
        }else{
            rowsInserted++;
        }
    }

    public void increaseRowsRejectedCount() throws MetricsException{
        if(startReadingMetrics){
            throw new MetricsException("Loader Metrics did not start. startMetrics() was not called.");
        }else{
            rowsRejected++;
        }
    }

    public void stopMetrics() throws MetricsException{
        if(startReadingMetrics){
            throw new MetricsException("Loader Metrics did not start. startMetrics() was not called.");
        }else{
            stopwatch.stop();
            bytesPerSecond = ( bytesProcessed / (float) stopwatch.elapsed(TimeUnit.SECONDS));
            rowsPerhour = ( (rowsInserted + rowsRejected ) /  ( (float) stopwatch.elapsed(TimeUnit.SECONDS) / (float) 3600) );
            startReadingMetrics = false;
        }
    }

    public String getrecordedMetrics() {
        return "LoaderMetrics{" +
           "rowsInserted=" + rowsInserted +
           ", rowsRejected=" + rowsRejected +
           ", bytesProcessed=" + bytesProcessed +
           ", bytesPerSecond=" + bytesPerSecond +
           ", rowsPerhour=" + rowsPerhour +
           ", stopwatch=" + stopwatch +
           ", startReadingMetrics=" + startReadingMetrics +
           '}';
    }

    class MetricsException extends Exception{
        private String exceptionMessage;

        public MetricsException(String exceptionMessage){
            this.exceptionMessage = exceptionMessage;
        }
    }
}
