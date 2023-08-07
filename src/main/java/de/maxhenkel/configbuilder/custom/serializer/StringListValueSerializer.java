package de.maxhenkel.configbuilder.custom.serializer;

import de.maxhenkel.configbuilder.custom.StringListValue;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

import java.util.ArrayList;
import java.util.List;

public class StringListValueSerializer implements ValueSerializer<StringListValue> {

    public static final StringListValueSerializer INSTANCE = new StringListValueSerializer();

    @Override
    public StringListValue deserialize(String str) {
        List<String> resultList = new ArrayList<>();
        for (String s : str.split("(?<!\\\\);")) {
            resultList.add(s.replace("\\;", ";"));
        }
        return StringListValue.of(resultList);
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
