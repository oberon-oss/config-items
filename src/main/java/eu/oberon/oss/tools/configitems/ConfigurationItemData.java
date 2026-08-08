package eu.oberon.oss.tools.configitems;

import org.jetbrains.annotations.Nullable;

import java.util.prefs.Preferences;

/**
 * Represents a configurable item that is part of a configuration system. Allows getting and setting the value of the configuration item, as well as persisting
 * it to and loading it from a {@link Preferences}references store.
 *
 * @param <I> The type of the identifier for this configuration item.
 * @param <A> The type of the value held by this configuration item.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface ConfigurationItemData<I, A> {
    /**
     * Retrieves the name of the configuration item.
     *
     * @return the name of the configuration item.
     */
    I getKey();

    /**
     * Sets the value of the configuration item.
     *
     * @param value the new value to set for the configuration item. This value must match the type parameter {@code <T>} defined for the configuration item.
     *
     * @since 1.0.0
     */
    void setCurrentValue(A value);

    /**
     * Retrieves the current value of the configuration item.
     *
     * @return the current value of the configuration item. The return type matches the generic type parameter {@code <T>} specified for the configuration
     *         item.
     *
     * @since 1.0.0
     */
    @Nullable A getCurrentValue();


    /**
     * Retrieves the default value of the configuration item.
     *
     * @return the default value of the configuration item. The return type matches the generic type parameter {@code <A>} specified for the configuration
     *         item.
     *
     * @since 1.0.0
     */
    @Nullable A getDefaultValue();
}
