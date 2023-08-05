package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IntegerListConfigEntry extends ConfigEntryImpl<List<Integer>> {

    public IntegerListConfigEntry(CommentedPropertyConfig config, String[] comments, String key, List<Integer> def) {
        super(config, comments, key, def);
        reload();
    }

    @Override
    @Nullable
    List<Integer> deserialize(String str) {
        List<Integer> list = new ArrayList<>();
        String[] split = str.split(",");
        for (String n : split) {
            try {
                list.add(Integer.parseInt(n));
            } catch (NumberFormatException e) {
            }
        }
        return list;
    }

    @Override
    String serialize(List<Integer> val) {
        return val.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
}
