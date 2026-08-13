package eu.oberon.oss.tools.configitems.storage;

import org.jetbrains.annotations.NotNull;

/**
 * Non-generic view of a registered configuration item.
 * <p>
 * This interface is useful for registry operations where the item is known to exist, but its full generic type signature is not known by the caller.
 * </p>
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface RegisteredConfigurationItem {

    /**
     * Returns the item key.
     *
     * @return The item key.
     *
     * @since 1.0.0
     */
    @NotNull Object getKey();

    /**
     * Retrieves the storage provider responsible for persisting and retrieving configuration items.
     *
     * @return The storage provider.
     *
     * @since 1.0.0
     */
    @NotNull StorageProvider storageProvider();

    /**
     * Loads the configuration item from persistent storage.
     *
     * @since 1.0.0
     */
    void load();

    /**
     * Saves the configuration item to persistent storage.
     *
     * @since 1.0.0
     */
    void save();
}