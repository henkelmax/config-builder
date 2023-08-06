package de.maxhenkel.configbuilder.custom;

import de.maxhenkel.configbuilder.entry.EntrySerializable;
import de.maxhenkel.configbuilder.entry.ValueSerializer;

import java.util.*;

/**
 * An unmodifiable string list that can be used as a config entry.
 * <br/>
 * The list is serialized as a string with the elements separated by a semicolon.
 */
@EntrySerializable(StringListValue.StringListEntrySerializer.class)
public class StringListValue implements List<String> {

    protected final List<String> list;

    protected StringListValue(String... values) {
        this(Arrays.asList(values));
    }

    protected StringListValue(List<String> values) {
        list = Collections.unmodifiableList(values);
    }

    public static StringListValue of(String... values) {
        return new StringListValue(values);
    }

    public static StringListValue of(List<String> values) {
        return new StringListValue(values);
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
    public Iterator<String> iterator() {
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
    public boolean add(String s) {
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
    public boolean addAll(Collection<? extends String> c) {
        return throwException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
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
    public String get(int index) {
        return list.get(index);
    }

    @Override
    public String set(int index, String element) {
        return throwException();
    }

    @Override
    public void add(int index, String element) {
        throwException();
    }

    @Override
    public String remove(int index) {
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
    public ListIterator<String> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<String> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    private static <T> T throwException() {
        throw new UnsupportedOperationException("Can't modify config entries");
    }

    static class StringListEntrySerializer implements ValueSerializer<StringListValue> {
        @Override
        public StringListValue deserialize(String str) {
            List<String> resultList = new ArrayList<>();
            for (String s : str.split("(?<!\\\\);")) {
                resultList.add(s.replace("\\;", ";"));
            }
            return new StringListValue(resultList);
        }

        @Override
        public String serialize(StringListValue val) {
            List<String> resultList = new ArrayList<>(val.size());
            for (String str : val) {
                resultList.add(str.replace(";", "\\;"));
            }
            return String.join(";", resultList);
        }
    }
}
