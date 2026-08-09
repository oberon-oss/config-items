package eu.oberon.oss.tools.configitems.items;

import java.util.function.Function;

/**
 * Provides mechanisms for accessing and transforming configuration item data, specifically facilitating type conversion between internal keys, external keys,
 * application data, and storage representations.
 *
 * @param <I> The type representing the internal key used for identification.
 * @param <K> The type representing the external key used for mapping or interaction.
 * @param <A> The type of the data as used by the application.
 * @param <P> The type of the data as stored in persistent storage.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface ConfigurationItemAccessor<I, K, A, P> {
    /**
     * Returns the internal key type.
     *
     * @return The internal key type.
     *
     * @since 1.0.0
     */
    Class<I> intKeyType();

    /**
     * Returns the external key type.
     *
     * @return The external key type.
     *
     * @since 1.0.0
     */
    Class<K> extKeyType();

    /**
     * Returns the application data type.
     *
     * @return The application data type.
     *
     * @since 1.0.0
     */
    Class<A> applicationDataType();

    /**
     * Returns the storage type.
     *
     * @return The storage type.
     *
     * @since 1.0.0
     */
    Class<P> storageType();

    /**
     * Returns the conversion function from an internal key to an external key.
     *
     * @return The conversion function from an internal key to an external key.
     *
     * @since 1.0.0
     */
    Function<I, K> toExtKey();

    /**
     * Returns the conversion function from a storage type to an application data type.
     *
     * @return The conversion function from a storage type to an application data type.
     *
     * @since 1.0.0
     */
    Function<P, A> toDataType();

    /**
     * Returns the conversion function from an application data type to a storage type.
     *
     * @return The conversion function from an application data type to a storage type.
     *
     * @since 1.0.0
     */
    Function<A, P> toStorageType();
}
