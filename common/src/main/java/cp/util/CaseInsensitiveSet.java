package cp.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of <tt>Set</tt> that accepts Strings, and manages them
 * in a case-insensitive fashion, yet preserves the casing that is provided.
 */
public class CaseInsensitiveSet implements Set<String> {

    // index of strings coerced to all lower case
    private final Set<String> index;

    private final Map<String, String> lookup;


    public CaseInsensitiveSet() {
        index = Sets.newTreeSet();
        lookup = Maps.newTreeMap();
    }

    @Override
    public int size() {
        return index.size();
    }

    @Override
    public boolean isEmpty() {
        return index.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return o != null && index.contains(((String) o).toLowerCase());
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {

            private Iterator<Map.Entry<String, String>> mapEntryIterator;

            {{
                mapEntryIterator = lookup.entrySet().iterator();
            }}

            @Override
            public boolean hasNext() {
                return mapEntryIterator.hasNext();
            }

            @Override
            public String next() {
                return mapEntryIterator.next().getValue();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove from this iterator");
            }
        };
    }

    @Override
    public Object[] toArray() {
        return lookup.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return lookup.values().toArray(a);
    }

    @Override
    public boolean add(String s) {
        String lower = s.toLowerCase();
        if (!index.contains(lower)) {
            index.add(lower);
            lookup.put(lower, s);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        String s = (String)o;
        String lower = s.toLowerCase();
        if (index.contains(lower)) {
            index.remove(lower);
            lookup.remove(lower);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        boolean added = false;
        for (String s : c) {
            added = add(s) || added;
        }
        return added;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Set<String> toBeRemoved = Sets.newTreeSet();
        Set<String> toBeRetained = Sets.newTreeSet();
        for (Object o : c) {
            toBeRetained.add(((String)o).toLowerCase());
        }
        for (String s : index) {
            if (!toBeRetained.contains(s)) {
                toBeRemoved.add(s);
            }
        }
        for (String s : toBeRemoved) {
            index.remove(s);
            lookup.remove(s);
        }
        return toBeRemoved.size() > 0;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removedAny = false;
        for (Object o : c) {
            boolean removedCurrent = remove(o);
            removedAny = removedAny || removedCurrent;
        }
        return removedAny;
    }

    @Override
    public void clear() {
        index.clear();
        lookup.clear();
    }
}
