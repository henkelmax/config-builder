package de.maxhenkel.configbuilder.custom.serializer;

import de.maxhenkel.configbuilder.custom.StringMap;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringMapValueSerializer implements ValueSerializer<StringMap> {

    public static final StringMapValueSerializer INSTANCE = new StringMapValueSerializer();

    public static final Pattern QUOTE_ESCAPE_PATTERN = Pattern.compile("\"((?:(?![\"\\\\]).|\\\\.)*)\"=\"((?:(?![\"\\\\]).|\\\\.)*)\"");

    @Nullable
    @Override
    public StringMap deserialize(String str) {
        boolean matches = QUOTE_ESCAPE_PATTERN.splitAsStream(str).allMatch(s -> s.trim().isEmpty() || s.trim().equals(","));
        if (!matches) {
            return null;
        }
        Map<String, String> map = new LinkedHashMap<>();
        Matcher matcher = QUOTE_ESCAPE_PATTERN.matcher(str);
        while (matcher.find()) {
            map.put(unescape(matcher.group(1)), unescape(matcher.group(2)));
        }
        return StringMap.of(map);
    }

    @Override
    public String serialize(StringMap val) {
        List<String> resultList = new ArrayList<>(val.size());
        for (Map.Entry<String, String> entry : val.entrySet()) {
            resultList.add("\"" + escape(entry.getKey()) + "\"" + "=" + "\"" + escape(entry.getValue()) + "\"");
        }
        return String.join(",", resultList);
    }

    private static String escape(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private static String unescape(String input) {
        return input
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

}
