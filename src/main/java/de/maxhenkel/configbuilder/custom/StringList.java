package de.maxhenkel.configbuilder.custom;

import java.util.*;

/**
 * An unmodifiable string list that can be used as a config entry.
 * <br/>
 * The list is serialized as a string with the elements separated by a semicolon.
 */
public class StringList extends AbstractValueList<String> {

    protected StringList(String... values) {
        super(values);
    }

    protected StringList(List<String> values) {
        super(values);
    }

    public static StringList of(String... values) {
        return new StringList(values);
    }

    public static StringList of(List<String> values) {
        return new StringList(values);
    }

}
