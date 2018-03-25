package loader.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;

/**
 * Based on the <i>Space-Saving</i> algorithm and the <i>Stream-Summary</i>
 * data structure as described in:
 * <i>Efficient Computation of Frequent and Top-k Elements in Data Streams</i>
 * by Metwally, Agrawal, and Abbadi
 * <p/>
 * Ideally used in multithreaded applications, otherwise see {@link StreamSummary}
 *
 * @param <T> type of data in the stream to be summarized
 * @author Eric Vlaanderen
 */
public class ConcurrentStreamSummary<T> implements ITopK<T> {

    private final int capacity;
    private final ConcurrentHashMap<T, ScoredItem<T>> itemMap;
    private final AtomicReference<ScoredItem<T>> minVal;
    private final AtomicLong size;
    private final AtomicBoolean reachCapacity;

    public ConcurrentStreamSummary(int capacity) {
        this.capacity = capacity;
        this.minVal = new AtomicReference<>();
        this.size = new AtomicLong(0);
        this.itemMap = new ConcurrentHashMap<>(capacity);
        this.reachCapacity = new AtomicBoolean(false);
    }

    @Override
    public boolean offer(T element) {
        return offer(element, 1);
    }

    @Override
    public boolean offer(T element, int incrementCount) {
        long val = incrementCount;
        ScoredItem<T> value = new ScoredItem<>(element, incrementCount);
        ScoredItem<T> oldVal = itemMap.putIfAbsent(element, value);

        if (oldVal != null) {
            val = oldVal.addAndGetCount(incrementCount);
        } else if (reachCapacity.get() || size.incrementAndGet() > capacity) {
            reachCapacity.set(true);

            ScoredItem<T> oldMinVal = minVal.getAndSet(value);
            itemMap.remove(oldMinVal.getItem());

            while (oldMinVal.isNewItem()) {
                // Wait for the oldMinVal so its error and value are completely up to date.
                // no thread.sleep here due to the overhead of calling it - the waiting time will be microseconds.
            }

            long count = oldMinVal.getCount();
            value.addAndGetCount(count);
            value.setError(count);
        }

        value.setNewItem(false);
        minVal.set(getMinValue());

        return val != incrementCount;
    }

    private ScoredItem<T> getMinValue() {
        ScoredItem<T> minVal = null;

        for (ScoredItem<T> entry : itemMap.values()) {
            if (minVal == null || (!entry.isNewItem() && entry.getCount() < minVal.getCount())) {
                minVal = entry;
            }
        }

        return minVal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (ScoredItem<T> entry : itemMap.values()) {
            sb.append('(').append(entry.getCount()).append(": ")
               .append(entry.getItem()).append(", e: ").append(entry.getError())
               .append("),");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public List<T> peek(int k) {
        List<T> toReturn = new ArrayList<>(k);
        List<ScoredItem<T>> values = peekWithScores(k);
        for (ScoredItem<T> value : values) {
            toReturn.add(value.getItem());
        }
        return toReturn;
    }

    public List<ScoredItem<T>> peekWithScores(int k) {
        List<ScoredItem<T>> values = new ArrayList<>();
        for (Map.Entry<T, ScoredItem<T>> entry : itemMap.entrySet()) {
            ScoredItem<T> value = entry.getValue();
            values.add(new ScoredItem<>(value.getItem(), value.getCount(), value.getError()));
        }
        Collections.sort(values);
        values = values.size() > k ? values.subList(0, k) : values;
        return values;
    }

    public static String formatSummary(ConcurrentStreamSummary<String> topk) {
        StringBuilder sb = new StringBuilder();

        List<ScoredItem<String>> counters = topk.peekWithScores(topk.capacity);
        String itemHeader = "item";
        String countHeader = "count";
        String errorHeader = "error";

        int maxItemLen = itemHeader.length();
        int maxCountLen = countHeader.length();
        int maxErrorLen = errorHeader.length();

        for (ScoredItem<String> counter : counters) {
            maxItemLen = Math.max(counter.getItem().length(), maxItemLen);
            maxCountLen = Math.max(Long.toString(counter.getCount()).length(), maxCountLen);
            maxErrorLen = Math.max(Long.toString(counter.getError()).length(), maxErrorLen);
        }

        sb.append(format("%n%" + maxItemLen + "s %" + maxCountLen + "s %" + maxErrorLen + "s%n",
           itemHeader, countHeader, errorHeader));
        sb.append(format("%" + maxItemLen + "s %" + maxCountLen + "s %" + maxErrorLen + "s%n",
           string('-', maxItemLen), string('-', maxCountLen), string('-', maxErrorLen)));

        for (ScoredItem<String> counter : counters) {
            sb.append(format("%" + maxItemLen + "s %" + maxCountLen + "d %" + maxErrorLen + "d%n",
               counter.getItem(), counter.getCount(), counter.getError()));
        }

        return sb.toString();
    }

    static String string(char c, int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(c);
        }

        return sb.toString();
    }
}
