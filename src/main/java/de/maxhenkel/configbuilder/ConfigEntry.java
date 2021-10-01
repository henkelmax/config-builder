package de.maxhenkel.configbuilder;

public interface ConfigEntry<T> {

    T get();

    ConfigEntry<T> set(T value);

    ConfigEntry<T> reset();

    ConfigEntry<T> save();

    ConfigEntry<T> saveSync();

    Config getConfig();

}
