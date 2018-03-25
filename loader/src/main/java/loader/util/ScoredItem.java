package loader.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Stolen from https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/stream/ScoredItem.java
 */
public class ScoredItem<T> implements Comparable<ScoredItem<T>> {

    private final AtomicLong error;
    private final AtomicLong count;
    private final AtomicBoolean newItem;
    private final T item;

    public ScoredItem(T item, long count, long error) {
        this.item = item;
        this.error = new AtomicLong(error);
        this.count = new AtomicLong(count);
        this.newItem = new AtomicBoolean(true);
    }

    public ScoredItem(T item, long count) {
        this(item, count, 0L);
    }

    public long addAndGetCount(long delta) {
        return this.count.addAndGet(delta);
    }

    public void setError(long newError) {
        this.error.set(newError);
    }

    public long getError() {
        return error.get();
    }

    public T getItem() {
        return item;
    }

    public boolean isNewItem() {
        return newItem.get();
    }

    public long getCount() {
        return count.get();
    }

    @Override
    public int compareTo(ScoredItem<T> o) {
        long x = o.count.get();
        long y = count.get();
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Value: ");
        sb.append(item);
        sb.append(", Count: ");
        sb.append(count);
        sb.append(", Error: ");
        sb.append(error);
        sb.append(", object: ");
        sb.append(super.toString());
        return sb.toString();
    }

    public void setNewItem(boolean newItem) {
        this.newItem.set(newItem);
    }
}
