package eu.oberon.oss.tools.configitems.storage;

import eu.oberon.oss.tools.configitems.items.ConfigurationItem;
import eu.oberon.oss.tools.configitems.items.ConfigurationItemAccessor;

/**
 * Represents a configuration item that can be stored and retrieved from a persistent storage. Extends {@link ConfigurationItem}, with additional functionality
 * to handle persistence through a {@link StorageProvider}.
 *
 * @param <I> The type of the internal key identifier for the configuration item.
 * @param <K> The type of the external key used for conversion.
 * @param <A> The type of the application-level data held by the configuration item.
 * @param <P> The type of the data representation in persistent storage.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface StorableConfigurationItem<I, K, A, P> extends ConfigurationItem<I, A>, RegisteredConfigurationItem {

    /**
     * Provides access to the configuration item accessor.
     *
     * @return A {@code ConfigurationItemAccessor} instance that provides access to type information, key conversion functions, and data mapping between the
     *         application layer and persistent storage for the configuration item.
     *
     * @since 1.0.0
     */
    ConfigurationItemAccessor<I, K, A, P> configurationItemAccessor();

    /**
     * Retrieves the storage provider responsible for persisting and retrieving configuration items.
     *
     * @return An instance of {@code StorageProvider} used for managing the storage operations of the configuration items, such as loading and storing them.
     *
     * @since 1.0.0
     */
    @Override
    StorageProvider storageProvider();

    /**
     * Loads the configuration item from persistent storage.
     *
     * @since 1.0.0
     */
    @Override
    default void load() {
        storageProvider().loadConfigurationItem(this);
    }

    /**
     * Saves the configuration item to persistent storage.
     *
     * @since 1.0.0
     */
    @Override
    default void save() {
        storageProvider().storeConfigurationItem(this);
    }
}
