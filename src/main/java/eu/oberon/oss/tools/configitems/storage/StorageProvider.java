package eu.oberon.oss.tools.configitems.storage;

/**
 * Interface for managing the storage and retrieval of configuration items in a persistent storage system. This provides generic methods for storing and loading
 * configuration items, as well as validation of allowable key and value types for storage.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface StorageProvider {
    /**
     * Stores the given configuration item in persistent storage.
     *
     * @param item The configuration item to be stored.
     * @param <I>  The type of the configuration item.
     * @param <K>  The type of the key used in the configuration item.
     * @param <A>  The type of the attribute used in the configuration item.
     * @param <P>  The type of the property used in the configuration item.
     *
     * @since 1.0.0
     */
    <I, K, A, P> void storeConfigurationItem(StorableConfigurationItem<I, K, A, P> item);

    /**
     * Loads the given configuration item from persistent storage.
     *
     * @param item The configuration item to be loaded.
     * @param <I>  The type of the configuration item.
     * @param <K>  The type of the key used in the configuration item.
     * @param <A>  The type of the attribute used in the configuration item.
     * @param <P>  The type of the property used in the configuration item.
     *
     * @since 1.0.0
     */
    <I, K, A, P> void loadConfigurationItem(StorableConfigurationItem<I, K, A, P> item);

    /**
     * Checks if the given key class type is allowed for storage.
     *
     * @param keyClass The key class type to be checked.
     *
     * @return true if the key class type is allowed, false otherwise.
     *
     * @since 1.0.0
     */
    boolean isKeyClassTypeAllowed(Class<?> keyClass);

    /**
     * Checks if the given value class type is allowed for storage.
     *
     * @param valueClass The value class type to be checked.
     *
     * @return true if the value class type is allowed, false otherwise.
     *
     * @since 1.0.0
     */
    boolean isStorageClassTypeAllowed(Class<?> valueClass);
}
