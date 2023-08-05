package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.Config;

public interface ConfigEntry<T> {

    /**
     * Sets the comments of the config entry
     *
     * @param comments the comments
     * @return the config entry
     */
    ConfigEntry<T> comment(String... comments);

    /**
     * @return the comments of the config entry
     */
    String[] getComments();

    /**
     * @return the current value of the config entry
     */
    T get();

    /**
     * Sets the value of the config entry
     *
     * @param value the new value
     * @return the config entry
     */
    ConfigEntry<T> set(T value);

    /**
     * @return the key of the config entry
     */
    String getKey();

    /**
     * Resets the config entry to the default value
     *
     * @return the config entry
     */
    ConfigEntry<T> reset();

    /**
     * Saves the config to the disk asynchronously
     * <br/>
     * Note that reloading the config immediately after saving it asynchronously may cause issues
     *
     * @return the config entry
     */
    ConfigEntry<T> save();

    /**
     * Saves the whole config to the disk synchronously
     * <br/>
     * Note that this method blocks the current thread until the config is saved
     *
     * @return the config entry
     */
    ConfigEntry<T> saveSync();

    /**
     * @return the default value of the config entry
     */
    T getDefault();

    /**
     * @return the underlying config of the config entry
     */
    Config getConfig();

}
