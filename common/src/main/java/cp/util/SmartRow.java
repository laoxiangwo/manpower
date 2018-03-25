package cp.util;

import com.datastax.driver.core.*;
import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Because the Datastax Row object is dumb and doesn't want to do autoboxing or smart null handling.
 * Won't protect you from bad indexing into the row though.
 */
public final class SmartRow {
    
    private final Row row;
    private final ColumnDefinitions columnDefinitions;

    public SmartRow(Row row) {
        this.row = row;
        this.columnDefinitions = row.getColumnDefinitions();
    }

    
    public ColumnDefinitions getColumnDefinitions() {
        return columnDefinitions;
    }

    
    public boolean isNull(int i) {
        return row.isNull(i);
    }

    
    public boolean isNull(String s) {
        return !columnDefinitions.contains(s) || row.isNull(s);
    }

    
    public boolean getBool(int i) {
        return row.getBool(i);
    }

    
    public Boolean getBool(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getBool(s) : null;
    }

    public int getInt(int i) {
        return row.getInt(i);
    }

    
    public Integer getInt(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getInt(s) : null;
    }

    
    public long getLong(int i) {
        return row.getLong(i);
    }

    
    public Long getLong(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getLong(s) : null;
    }

    
    public Date getDate(int i) {
        return row.getDate(i);
    }

    
    public Date getDate(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getDate(s) : null;
    }

    
    public float getFloat(int i) {
        return row.getFloat(i);
    }

    
    public Float getFloat(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getFloat(s) : null;
    }

    
    public double getDouble(int i) {
        return row.getDouble(i);
    }

    
    public Double getDouble(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.isNull(s) ? null : row.getDouble(s) : null;
    }

    
    public ByteBuffer getBytesUnsafe(int i) {
        return row.getBytesUnsafe(i);
    }

    
    public ByteBuffer getBytesUnsafe(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getBytesUnsafe(s) : null;
    }

    
    public ByteBuffer getBytes(int i) {
        return row.getBytes(i);
    }

    
    public ByteBuffer getBytes(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getBytes(s) : null;
    }

    
    public String getString(int i) {
        return row.getString(i);
    }

    
    public String getString(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getString(s) : null;
    }

    
    public BigInteger getVarint(int i) {
        return row.getVarint(i);
    }

    
    public BigInteger getVarint(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getVarint(s) : null;
    }

    
    public BigDecimal getDecimal(int i) {
        return row.getDecimal(i);
    }

    
    public BigDecimal getDecimal(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getDecimal(s) : null;
    }

    
    public UUID getUUID(int i) {
        return null;
    }

    
    public UUID getUUID(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getUUID(s) : null;
    }

    
    public InetAddress getInet(int i) {
        return null;
    }

    
    public InetAddress getInet(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getInet(s) : null;
    }

    
    public Token getToken(int i) {
        return null;
    }

    
    public Token getToken(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getToken(s) : null;
    }

    
    public Token getPartitionKeyToken() {
        return row.getPartitionKeyToken();
    }

    
    public <T> List<T> getList(int i, Class<T> aClass) {
        return row.getList(i, aClass);
    }

    
    public <T> List<T> getList(int i, TypeToken<T> typeToken) {
        return row.getList(i, typeToken);
    }

    
    public <T> List<T> getList(String s, Class<T> aClass) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getList(s, aClass) : null;
    }

    
    public <T> List<T> getList(String s, TypeToken<T> typeToken) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getList(s, typeToken) : null;
    }

    
    public <T> Set<T> getSet(int i, Class<T> aClass) {
        return row.getSet(i, aClass);
    }

    
    public <T> Set<T> getSet(int i, TypeToken<T> typeToken) {
        return row.getSet(i, typeToken);
    }

    
    public <T> Set<T> getSet(String s, Class<T> aClass) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getSet(s, aClass) : null;
    }

    
    public <T> Set<T> getSet(String s, TypeToken<T> typeToken) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getSet(s, typeToken) : null;
    }

    
    public <K, V> Map<K, V> getMap(int i, Class<K> aClass, Class<V> aClass1) {
        return row.getMap(i, aClass, aClass1);
    }

    
    public <K, V> Map<K, V> getMap(int i, TypeToken<K> typeToken, TypeToken<V> typeToken1) {
        return row.getMap(i, typeToken, typeToken1);
    }

    
    public UDTValue getUDTValue(int i) {
        return row.getUDTValue(i);
    }

    
    public TupleValue getTupleValue(int i) {
        return row.getTupleValue(i);
    }

    
    public Object getObject(int i) {
        return row.getObject(i);
    }

    
    public <K, V> Map<K, V> getMap(String s, Class<K> aClass, Class<V> aClass1) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getMap(s, aClass, aClass1) : null;
    }

    
    public <K, V> Map<K, V> getMap(String s, TypeToken<K> typeToken, TypeToken<V> typeToken1) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getMap(s, typeToken, typeToken1) : null;
    }

    
    public UDTValue getUDTValue(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getUDTValue(s) : null;
    }

    
    public TupleValue getTupleValue(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getTupleValue(s) : null;
    }

    
    public Object getObject(String s) {
        return columnDefinitions.contains(s) ? row.isNull(s) ? null : row.getObject(s) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        SmartRow smartRow = (SmartRow) o;
        return Objects.equal(row, smartRow.row);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(row);
    }

    @Override
    public String toString() {
        return ObjectUtil.toStringHelper(this)
                .add("columnDefinitions", columnDefinitions)
                .add("row", row)
                .toString();
    }
}
