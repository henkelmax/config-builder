package de.maxhenkel.configbuilder.custom;

import java.util.List;

/**
 * An unmodifiable integer list that can be used as a config entry.
 * <br/>
 * The list is serialized as a string with the elements separated by a comma.
 */
public class IntegerList extends AbstractValueList<Integer> {

    protected IntegerList(Integer... values) {
        super(values);
    }

    protected IntegerList(List<Integer> values) {
        super(values);
    }

    public static IntegerList of(Integer... values) {
        return new IntegerList(values);
    }

    public static IntegerList of(List<Integer> values) {
        return new IntegerList(values);
    }

}
