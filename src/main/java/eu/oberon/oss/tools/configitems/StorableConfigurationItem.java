package eu.oberon.oss.tools.configitems;

import java.util.function.Function;

public interface StorableConfigurationItem<I,K,A,P> {
    ConfigurationItemData<I,A> getConfigurationItemData();

    /**
     * Returns the internal key type.
     *
     * @return The internal key type.
     *
     * @since 1.0.0
     */
    Class<I> getIntKeyType();

    /**
     * Returns the external key type.
     *
     * @return The external key type.
     *
     * @since 1.0.0
     */
    Class<K> getExtKeyType();

    /**
     * Returns the application data type.
     *
     * @return The application data type.
     *
     * @since 1.0.0
     */
    Class<A> getApplicationDataType();

    /**
     * Returns the storage type.
     *
     * @return The storage type.
     *
     * @since 1.0.0
     */
    Class<P> getStorageType();

    /**
     * Returns the conversion function from an internal key to an external key.
     *
     * @return The conversion function from an internal key to an external key.
     *
     * @since 1.0.0
     */
    Function<I, K> getToExtKey();

    /**
     * Returns the conversion function from an external key to an internal key.
     *
     * @return The conversion function from an external key to an internal key.
     *
     * @since 1.0.0
     */
    Function<K, I> getToIntKey();

    /**
     * Returns the conversion function from a storage type to an application data type.
     *
     * @return The conversion function from a storage type to an application data type.
     *
     * @since 1.0.0
     */
    Function<P, A> getToDataType();

    /**
     * Returns the conversion function from an application data type to a storage type.
     *
     * @return The conversion function from an application data type to a storage type.
     *
     * @since 1.0.0
     */
    Function<A, P> getToStorageType();

    /**
     * Returns the item ID.
     *
     * @return The item ID.
     *
     * @since 1.0.0
     */
    I getItemID();

    /**
     * Returns the default value.
     *
     * @return The default value.
     *
     * @since 1.0.0
     */
    A getDefaultValue();
}
