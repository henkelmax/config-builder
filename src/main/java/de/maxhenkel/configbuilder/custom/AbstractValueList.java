package de.maxhenkel.configbuilder.custom;

import java.util.*;

/**
 * An unmodifiable list intended to be used as a config entry.
 */
public abstract class AbstractValueList<T> implements List<T> {

    protected final List<T> list;

    protected AbstractValueList(T... values) {
        this(Arrays.asList(values));
    }

    protected AbstractValueList(List<T> values) {
        list = Collections.unmodifiableList(values);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T s) {
        return throwException();
    }

    @Override
    public boolean remove(Object o) {
        return throwException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return throwException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return throwException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return throwException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return throwException();
    }

    @Override
    public void clear() {
        throwException();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        return throwException();
    }

    @Override
    public void add(int index, T element) {
        throwException();
    }

    @Override
    public T remove(int index) {
        return throwException();
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractValueList<?> that = (AbstractValueList<?>) o;
        return Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    private static <T> T throwException() {
        throw new UnsupportedOperationException("Can't modify config entries");
    }

}
