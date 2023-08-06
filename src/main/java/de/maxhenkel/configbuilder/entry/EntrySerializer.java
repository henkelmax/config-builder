package de.maxhenkel.configbuilder.entry;

import javax.annotation.Nullable;

public interface EntrySerializer<T> {

    /**
     * Deserializes the string to {@link T}
     *
     * @param str the string to deserialize
     * @return the deserialized value
     */
    @Nullable
    T deserialize(String str);

    /**
     * Serializes the value to a string
     *
     * @param val the value to serialize
     * @return the serialized value
     */
    @Nullable
    String serialize(T val);

}
