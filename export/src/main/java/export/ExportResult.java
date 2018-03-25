package export;

import cp.util.DurationUtil;
import com.google.common.base.MoreObjects;

/**
 * Encapsulation of export results
 *
 */
public class ExportResult {

    private boolean succeeded;

    private long lineCount;

    private Long durationInMillis;

    public boolean isSucceeded() {
        return succeeded;
    }

    public ExportResult setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
        return this;
    }

    public long getLineCount() {
        return lineCount;
    }

    public ExportResult setLineCount(long lineCount) {
        this.lineCount = lineCount;
        return this;
    }

    public Long durationInMillis() {
        return durationInMillis;
    }

    public ExportResult setDurationInMillis(Long durationInMillis) {
        this.durationInMillis = durationInMillis;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("succeeded", succeeded)
                .add("lineCount", lineCount)
                .add("duration", DurationUtil.formatDuration(durationInMillis))
                .toString();
    }
}
