package eu.oberon.oss.tools.configitems.storage.providers;

import eu.oberon.oss.tools.configitems.storage.StorableConfigurationItem;

import java.util.prefs.BackingStoreException;

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


    /**
     * Instructs the registry to flush any pending changes to the underlying storage.
     * <p>
     * This method operates on a stable snapshot of the registry. Items registered, removed, or replaced while this method is running are not guaranteed to
     * affect this invocation.
     *
     * @throws UnsupportedOperationException if the storage provider does not support flushing.
     * @throws BackingStoreException         if an error occurs while flushing the storage.
     * @since 1.0.0
     */
    default void flush() throws BackingStoreException {
        throw new UnsupportedOperationException("Flushing is not supported by this storage provider implementation.");
    }
}
