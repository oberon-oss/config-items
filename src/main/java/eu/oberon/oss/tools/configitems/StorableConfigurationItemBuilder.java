package eu.oberon.oss.tools.configitems;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Builder interface for creating instances of {@link DefaultStorableConfigurationItem}. This interface provides a fluent API for configuring all aspects of a
 * storable configuration item, including key types (internal and external), data types (application and storage), default values, and conversion functions
 * between different type representations.
 * <p>
 * The builder pattern allows for step-by-step construction of complex configuration items with proper type safety and validation. All setter methods return the
 * builder instance to enable method chaining.
 * </p>
 * <p>
 * All setter methods require non-null arguments to ensure that the configuration item is properly defined.
 *
 * @param <K> The external key type used to identify the configuration item externally (e.g., in user-facing APIs)
 * @param <A> The application data type - the type of value used within the application logic
 * @param <P> The storage/persistence type - the type used when storing the value
 * @param <I> The internal key type used to identify the configuration item internally
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface StorableConfigurationItemBuilder<I, K, A, P> {

    /**
     * Sets the external key type for the configuration item. The external key type is used for external identification, such as in user-facing APIs or
     * configuration files.
     *
     * @param extKeyType the class representing the external key type
     *
     * @return the builder instance for method chaining
     *
     * @since 1.0.0
     */
    StorableConfigurationItemBuilder<I, K, A, P> setExtKeyType(@NotNull Class<K> extKeyType);

    /**
     * Sets the application data type for the configuration item.
     *
     * @param applicationDataType the class representing the application data type
     *
     * @return the builder instance for method chaining
     *
     * @since 1.0.0
     */
    StorableConfigurationItemBuilder<I, K, A, P> setApplicationDataType(@NotNull Class<A> applicationDataType);

    /**
     * Sets the default value for the configuration item. This value will be used when no stored value is available or when the configuration is reset.
     *
     * @param defaultValue the default value in the application data type format
     *
     * @return the builder instance for method chaining
     *
     * @since 1.0.0
     */
    StorableConfigurationItemBuilder<I, K, A, P> setDefaultValue(@NotNull A defaultValue);

    /**
     * Sets the storage type for the configuration item.
     *
     * @param storageType the class representing the storage/persistence type
     *
     * @return the builder instance for method chaining
     *
     * @since 1.0.0
     */
    StorableConfigurationItemBuilder<I, K, A, P> setStorageType(@NotNull Class<P> storageType);

    /**
     * Sets the conversion function from an internal key to an external key. This function is used to transform the internal key representation to the external
     * key representation.
     *
     * @param toExtKey the function that converts from internal key type to external key type
     *
     * @return the builder instance for method chaining
     *
     * @since 1.0.0
     */
    StorableConfigurationItemBuilder<I, K, A, P> setToExtKey(@NotNull Function<I, K> toExtKey);

    /**
     * Sets the conversion function from an external key to an internal key. This function is used to transform the external key representation to the internal
     * key representation.
     *
     * @param toIntKey the function that converts from external key type to internal key type
     *
     * @return the builder instance for method chaining
     *
     * @since 1.0.0
     */
    StorableConfigurationItemBuilder<I, K, A, P> setToIntKey(@NotNull Function<K, I> toIntKey);

    /**
     * Sets the unique identifier for this configuration item. The item ID is used to uniquely identify this configuration item within the system.
     *
     * @param itemID the unique identifier in internal key type format
     *
     * @return the builder instance for method chaining
     *
     * @since 1.0.0
     */
    StorableConfigurationItemBuilder<I, K, A, P> setItemID(@NotNull I itemID);

    /**
     * Sets the conversion function from storage type to application data type. This function is used when loading stored values to convert them into the
     * application data format.
     *
     * @param toDataType the function that converts from storage type to application data type
     *
     * @return the builder instance for method chaining
     *
     * @since 1.0.0
     */
    StorableConfigurationItemBuilder<I, K, A, P> setToDataType(@NotNull Function<P, A> toDataType);

    /**
     * Sets the conversion function from an application data type to a storage type. This function is used when persisting values to convert them from the
     * application data format to the storage format.
     *
     * @param toStorageType the function that converts from an application data type to a storage type
     *
     * @return the builder instance for method chaining
     *
     * @since 1.0.0
     */
    StorableConfigurationItemBuilder<I, K, A, P> setToStorageType(@NotNull Function<A, P> toStorageType);

    /**
     * Builds and returns a new instance of {@link DefaultStorableConfigurationItem} with all configured properties. This method performs validation to ensure
     * all required properties have been set and creates the final immutable configuration item.
     *
     * @return a new {@link DefaultStorableConfigurationItem} instance with all configured properties
     *
     * @throws NullPointerException     if any required property has not been set
     * @throws IllegalArgumentException if any property value is invalid
     * @since 1.0.0
     */
    StorableConfigurationItem<I, K, A, P> build();
}
