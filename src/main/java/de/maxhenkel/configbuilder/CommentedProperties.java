package de.maxhenkel.configbuilder;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CommentedProperties implements Map<String, String> {

    private final boolean strict;
    private final List<String> headerComments;
    private final Map<String, Property> properties;

    /**
     * @param strict if this should be compliant with Javas {@link java.util.Properties} implementation
     */
    public CommentedProperties(boolean strict) {
        this.strict = strict;
        this.headerComments = new ArrayList<>();
        this.properties = new LinkedHashMap<>();
    }

    /**
     * Creates a new instance with strict mode enabled.
     */
    public CommentedProperties() {
        this(true);
    }

    /**
     * Adds the provided comment to the header comments.
     *
     * @param comment the comment
     * @return this
     */
    public CommentedProperties addHeaderComment(String comment) {
        headerComments.add(comment);
        return this;
    }

    /**
     * Clears all previously set header comments and adds the provided comments.
     *
     * @param headerComments the header comments
     * @return this
     */
    public CommentedProperties setHeaderComments(List<String> headerComments) {
        this.headerComments.clear();
        this.headerComments.addAll(headerComments);
        return this;
    }

    void sort(Comparator<String> comparator) {
        List<Map.Entry<String, Property>> list = new ArrayList<>(properties.entrySet());
        list.sort((o1, o2) -> comparator.compare(o1.getKey(), o2.getKey()));

        properties.clear();
        for (Map.Entry<String, Property> entry : list) {
            properties.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @param key the key
     * @return the value or <code>null</code> if the entry does not exist
     */
    @Nullable
    public String get(String key) {
        Objects.requireNonNull(key);
        Property property = properties.get(key);
        if (property == null) {
            return null;
        }
        return property.value;
    }

    /**
     * @param key the key
     * @return all comments for the entry or <code>null</code> if the entry does not exist
     */
    @Nullable
    public List<String> getComments(String key) {
        Objects.requireNonNull(key);
        Property property = properties.get(key);
        if (property == null) {
            return null;
        }
        return property.comments;
    }

    /**
     * @param key      the key
     * @param comments the comments for the entry
     * @return this
     */
    public CommentedProperties setComments(String key, List<String> comments) {
        Objects.requireNonNull(key);
        Property property = properties.get(key);
        if (property == null) {
            properties.put(key, new Property(comments, ""));
            return this;
        }
        property.comments = comments;
        return this;
    }

    /**
     * @param key      the key
     * @param value    the value
     * @param comments the comments for the entry
     * @return this
     */
    public CommentedProperties set(String key, String value, String... comments) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        properties.put(key, new Property(Arrays.asList(comments), value));
        return this;
    }

    /**
     * Loads the properties from the provided file.
     *
     * @param inputStream the input stream to read from
     * @return this
     * @throws IOException if an IO error occurs
     */
    public CommentedProperties load(InputStream inputStream) throws IOException {
        List<String> headerComments = new ArrayList<>();
        Map<String, Property> properties = new LinkedHashMap<>();
        try (LineReader reader = LineReader.fromInputStream(inputStream)) {
            boolean header = true;
            List<String> previousComments = new ArrayList<>();
            String line;
            while ((line = reader.nextLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (header) {
                        headerComments.addAll(previousComments);
                        previousComments.clear();
                        header = false;
                    }
                    continue;
                }
                Pair pair = readLine(line);
                if (pair.key == null) {
                    previousComments.add(pair.value);
                } else {
                    Property property = new Property(pair.value);
                    property.comments.addAll(previousComments);
                    previousComments.clear();
                    properties.put(pair.key, property);
                    header = false;
                }
            }
        }

        setHeaderComments(headerComments);
        this.properties.clear();
        this.properties.putAll(properties);
        return this;
    }

    protected static Pair readLine(String line) throws IOException {
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        StringReader reader = new StringReader(line);

        boolean hasBackslashAppeared = false;
        boolean isKey = true;
        boolean isComment = false;
        boolean isPrecedingBackslash = false;
        boolean onlyHadWhitespace = true;
        boolean isStartOfValue = false;
        int c;
        while ((c = reader.read()) != -1) {
            boolean isWhitespace = isWhitespace(c);
            if (isComment) {
                if (onlyHadWhitespace && isWhitespace) {
                    continue;
                } else {
                    onlyHadWhitespace = false;
                }
                value.append((char) c);
                continue;
            }
            if (isPrecedingBackslash) {
                if (isKey) {
                    key.append((char) readEscapedCharacter(c, reader));
                } else {
                    value.append((char) readEscapedCharacter(c, reader));
                }
                isPrecedingBackslash = false;
                continue;
            }
            if (c == '\\') {
                isPrecedingBackslash = true;
                hasBackslashAppeared = true;
                continue;
            }
            if (c == '#' || c == '!') {
                if (onlyHadWhitespace) {
                    isComment = true;
                    continue;
                }
            }
            if (isKey) {
                if (key.length() <= 0) {
                    if (isWhitespace(c)) {
                        continue;
                    }
                }
                if (isSeparator(c)) {
                    isKey = false;
                    isStartOfValue = true;
                    onlyHadWhitespace = false;
                    continue;
                }
                if (isWhitespace(c)) {
                    continue;
                }
                key.append((char) c);
            } else {
                if (isStartOfValue && !hasBackslashAppeared) {
                    if (isWhitespace(c) || isSeparator(c)) {
                        continue;
                    }
                }
                value.append((char) c);
                isStartOfValue = false;
            }
            if (onlyHadWhitespace) {
                if (!isWhitespace) {
                    onlyHadWhitespace = false;
                }
            }
        }
        return new Pair(isComment ? null : key.toString(), value.toString());
    }

    private static int readEscapedCharacter(int c, StringReader reader) throws IOException {
        if (c == 'u') {
            int u = 0;
            for (int i = 0; i < 4; i++) {
                int uc = reader.read();
                if (uc == -1) {
                    throw new IOException("Invalid unicode escape sequence");
                }
                u = u << 4;
                if (uc >= '0' && uc <= '9') {
                    u += uc - '0';
                } else if (uc >= 'a' && uc <= 'f') {
                    u += uc - 'a' + 10;
                } else if (uc >= 'A' && uc <= 'F') {
                    u += uc - 'A' + 10;
                } else {
                    throw new IOException("Invalid unicode escape sequence");
                }
            }
            return u;
        } else if (c == 't') {
            return '\t';
        } else if (c == 'r') {
            return '\r';
        } else if (c == 'n') {
            return '\n';
        } else if (c == 'f') {
            return '\f';
        } else {
            return c;
        }
    }

    private static boolean isWhitespace(int c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\f' || Character.isWhitespace(c);
    }

    private static boolean isSeparator(int c) {
        return c == '=' || c == ':' || c == ' ' || c == '\t' || c == '\f';
    }

    /**
     * Saves the properties to the provided output stream.
     *
     * @param outputStream the output stream to write to
     * @return this
     */
    public CommentedProperties save(OutputStream outputStream) {
        try (PrintWriter writer = new PrintWriter(outputStream)) {
            for (String comment : removeNewLines(headerComments)) {
                writer.print("# ");
                writer.println(comment);
            }
            if (headerComments.size() > 0) {
                writer.println();
            }
            for (Map.Entry<String, Property> entry : properties.entrySet()) {
                for (String comment : removeNewLines(entry.getValue().comments)) {
                    writer.print("# ");
                    writer.println(comment);
                }
                writer.print(escapeKey(entry.getKey()));
                writer.print("=");
                writer.println(escapeValue(entry.getValue().value));
            }
            writer.flush();
        }
        return this;
    }

    private static List<String> removeNewLines(List<String> comments) {
        List<String> newComments = new ArrayList<>();
        for (String comment : comments) {
            newComments.addAll(Arrays.asList(comment.split("\\r?\\n")));
        }
        return newComments;
    }

    private String escapeKey(String str) {
        str = escape(str);
        str = str.replace(" ", "\\ ");
        str = str.replace("=", "\\=");
        str = str.replace(":", "\\:");
        return str;
    }

    private String escapeValue(String str) {
        str = escape(str);
        if (strict) {
            str = str.replace("=", "\\=");
            str = str.replace(":", "\\:");
        }
        if (str.startsWith(" ")) {
            str = String.format("\\%s", str);
        }
        return str;
    }

    private String escape(String str) {
        str = str.replace("\\", "\\\\");
        str = str.replace("\n", "\\n");
        str = str.replace("\r", "\\n");
        str = str.replace("\t", "\\t");
        str = str.replace("#", "\\#");
        str = str.replace("!", "\\!");

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
                str = String.format("%s\\u%04X%s", str.substring(0, i), (int) c, str.substring(i + 1));
            }
        }

        return str;
    }

    /**
     * @return the number of properties
     */
    @Override
    public int size() {
        return properties.size();
    }

    /**
     * @return true if there are no properties
     */
    @Override
    public boolean isEmpty() {
        return properties.isEmpty();
    }

    /**
     * @param key the key
     * @return if the key is present
     */
    @Override
    public boolean containsKey(Object key) {
        return properties.containsKey(key);
    }

    /**
     * @param value the value
     * @return if the value is present
     * @deprecated this method is not performant and only present to satisfy the Map interface
     */
    @Override
    @Deprecated
    public boolean containsValue(Object value) {
        for (Property property : properties.values()) {
            if (property.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param key the key
     * @return the value or <code>null</code> if the key is not a string
     * @deprecated use {@link #get(String)} instead
     */
    @Override
    @Nullable
    @Deprecated
    public String get(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        return get((String) key);
    }

    /**
     * @param key   the key
     * @param value the value
     * @return the previous value or <code>null</code> if there was no previous value
     * @deprecated use {@link #set(String, String, String...)}} instead
     */
    @Override
    @Deprecated
    public String put(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Property put = properties.put(key, new Property(value));
        if (put == null) {
            return null;
        }
        return put.value;
    }

    /**
     * @param key the key
     * @return the previous value or <code>null</code> if there was no previous value
     */
    @Override
    public String remove(Object key) {
        Property removed = properties.remove(key);
        if (removed == null) {
            return null;
        }
        return removed.value;
    }

    /**
     * Adds all the properties from the provided map.
     *
     * @param map the map
     * @deprecated use {@link #set(String, String, String...)} instead
     */
    @Override
    @Deprecated
    public void putAll(Map<? extends String, ? extends String> map) {
        for (Map.Entry<? extends String, ? extends String> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Clears all the properties and comments.
     */
    @Override
    public void clear() {
        headerComments.clear();
        properties.clear();
    }

    /**
     * @return all property keys
     */
    @Override
    public Set<String> keySet() {
        return properties.keySet();
    }

    /**
     * @return all property values
     * @deprecated this method is not performant and only present to satisfy the Map interface
     */
    @Override
    @Deprecated
    public Collection<String> values() {
        return properties.values().stream().map(property -> property.value).collect(Collectors.toList());
    }

    /**
     * @return all properties
     * @deprecated this method is not performant and only present to satisfy the Map interface
     */
    @Override
    @Deprecated
    public Set<Entry<String, String>> entrySet() {
        return properties.entrySet().stream().map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().value)).collect(Collectors.toSet());
    }

    protected static class Property {
        private List<String> comments;
        private String value;

        public Property(List<String> comments, String value) {
            this.comments = comments;
            this.value = value;
        }

        public Property(String value) {
            this(new ArrayList<>(), value);
        }
    }

    protected static class LineReader implements Closeable {

        private final BufferedReader reader;

        public LineReader(BufferedReader reader) {
            this.reader = reader;
        }

        public static LineReader fromInputStream(InputStream inputStream) {
            return new LineReader(new BufferedReader(new InputStreamReader(inputStream)));
        }

        @Nullable
        public String nextLine() throws IOException {
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            if (line.endsWith("\\")) {
                line = line.substring(0, line.length() - 1);
                String nextLine = nextLine();
                if (nextLine == null) {
                    return line;
                }
                return line + nextLine;
            }
            return line;
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }

    protected static class Pair {
        private String key;
        private String value;

        public Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
