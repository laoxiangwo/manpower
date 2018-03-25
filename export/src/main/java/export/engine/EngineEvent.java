package export.engine;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * Small class to pass engine events to other threads
 *
 * Created by shengli on 12/27/15.
 */
public class EngineEvent implements Serializable {
    private static final long serialVersionUID = -3093504931422290327L;
    private final long rowCount;
    private final long charCount;
    private final long elapsedTimeMillis;

    public EngineEvent(long rowCount, long charCount, long elapsedTimeMillis) {
        this.rowCount = rowCount;
        this.charCount = charCount;
        this.elapsedTimeMillis = elapsedTimeMillis;
    }

    public long rowCount() {
        return rowCount;
    }

    public long charCount() {
        return charCount;
    }

    public long elapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineEvent that = (EngineEvent) o;
        return rowCount == that.rowCount &&
                charCount == that.charCount &&
                elapsedTimeMillis == that.elapsedTimeMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rowCount, charCount, elapsedTimeMillis);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("rowCount", rowCount)
                .add("charCount", charCount)
                .add("elapsedTimeMillis", elapsedTimeMillis)
                .toString();
    }
}
