package de.maxhenkel.configbuilder.custom;

import java.util.*;

/**
 * An unmodifiable map intended to be used as a config entry.
 */
public class AbstractValueMap<K, V> implements Map<K, V> {

    protected final Map<K, V> map;

    protected AbstractValueMap(Map<K, V> map) {
        this.map = Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return throwException();
    }

    @Override
    public V remove(Object key) {
        return throwException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throwException();
    }

    @Override
    public void clear() {
        throwException();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractValueMap<?, ?> that = (AbstractValueMap<?, ?>) o;

        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    private static <T> T throwException() {
        throw new UnsupportedOperationException("Can't modify config entries");
    }

    public abstract static class Builder<K, V, M extends AbstractValueMap<K, V>> {

        protected final Map<K, V> map;

        protected Builder() {
            this.map = new LinkedHashMap<>();
        }

        public Builder<K, V, M> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public Builder<K, V, M> putAll(Map<K, V> map) {
            this.map.putAll(map);
            return this;
        }

        public abstract M build();
    }

}
