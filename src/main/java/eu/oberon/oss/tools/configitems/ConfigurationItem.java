package eu.oberon.oss.tools.configitems;

import java.util.prefs.Preferences;

/**
 * Represents a configurable item that is part of a configuration system. Allows getting and setting the value of the configuration item, as well as persisting
 * it to and loading it from a {@link Preferences}references store.
 *
 * @param <T> The type of the value held by this configuration item.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface ConfigurationItem<T> {
    /**
     * Retrieves the name of the configuration item.
     *
     * @return the name of the configuration item.
     */
    String getItemName();

    /**
     * Sets the value of the configuration item.
     *
     * @param value the new value to set for the configuration item. This value must match the type parameter {@code <T>} defined for the configuration item.
     *
     * @since 1.0.0
     */
    void setConfigItemValue(T value);

    /**
     * Retrieves the current value of the configuration item.
     *
     * @return the current value of the configuration item. The return type matches the generic type parameter {@code <T>} specified for the configuration item.
     *
     * @since 1.0.0
     */
    T getConfigItemValue();

    /**
     * Persists the current state of the configuration item to the specified {@link Preferences} store.
     *
     * @param preferences the {@link Preferences} instance to which the configuration item's state should be stored. Must not be null.
     *
     * @since 1.0.0
     */
    void store(Preferences preferences);

    /**
     * Loads the state of the configuration item from the specified {@link Preferences} store.
     *
     * @param preferences the {@link Preferences} instance from which the configuration item's state should be loaded. Must not be null.
     *
     * @since 1.0.0
     */
    void load(Preferences preferences);
}
