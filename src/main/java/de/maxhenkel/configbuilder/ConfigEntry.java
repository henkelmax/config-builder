package de.maxhenkel.configbuilder;

public interface ConfigEntry<T> {

    ConfigEntry<T> comment(String... comments);

    String[] getComments();

    T get();

    ConfigEntry<T> set(T value);

    ConfigEntry<T> reset();

    ConfigEntry<T> save();

    ConfigEntry<T> saveSync();

    T getDefault();

    Config getConfig();

}
