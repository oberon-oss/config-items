package eu.oberon.oss.tools.configitems.storage;

import eu.oberon.oss.tools.configitems.items.ConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.providers.StorageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

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
public interface StorableConfigurationItem<I, K, A, P> extends ConfigurationItem<I, A, P>, RegisteredConfigurationItem {

    /**
     * Returns the internal key type.
     *
     * @return The internal key type.
     *
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    Class<I> intKeyType();

    /**
     * Returns the external key type.
     *
     * @return The external key type.
     *
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    Class<K> extKeyType();

    /**
     * Returns the conversion function from an internal key to an external key.
     *
     * @return The conversion function from an internal key to an external key.
     *
     * @since 1.0.0
     */
    Function<I, K> toExtKey();

    /**
     * Retrieves the storage provider responsible for persisting and retrieving configuration items.
     *
     * @return An instance of {@code StorageProvider} used for managing the storage operations of the configuration items, such as loading and storing them.
     *
     * @since 1.0.0
     */
    @Override
    @NotNull StorageProvider storageProvider();

    /**
     * Loads the configuration item from persistent storage.
     *
     * @since 1.0.0
     */
    @Override
    default void load() {
        storageProvider().loadConfigurationItem(this);
        clearUnsavedChanges();
    }

    /**
     * Saves the configuration item to persistent storage.
     *
     * @since 1.0.0
     */
    @Override
    default void save() {
        storageProvider().storeConfigurationItem(this);
        clearUnsavedChanges();
    }


}
