package de.maxhenkel.configbuilder.custom.serializer;

import de.maxhenkel.configbuilder.custom.IntegerList;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IntegerListValueSerializer implements ValueSerializer<IntegerList> {

    public static final IntegerListValueSerializer INSTANCE = new IntegerListValueSerializer();

    @Nullable
    @Override
    public IntegerList deserialize(String str) {
        List<Integer> resultList = new ArrayList<>();
        for (String s : str.split(",")) {
            try {
                resultList.add(Integer.valueOf(s));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return IntegerList.of(resultList);
    }

    @Override
    public String serialize(IntegerList val) {
        List<String> resultList = new ArrayList<>(val.size());
        for (Integer i : val) {
            resultList.add(String.valueOf(i));
        }
        return String.join(",", resultList);
    }

}
