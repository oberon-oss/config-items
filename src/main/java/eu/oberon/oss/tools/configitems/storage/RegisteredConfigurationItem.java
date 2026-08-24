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

    /**
     * Determines if there are any changes to the configuration item that have not been saved to persistent storage.
     *
     * @return true if there are unsaved changes, false otherwise.
     *
     * @since 1.0.0
     */
    boolean hasUnsavedChanges();

    /**
     * Marks the current state of this configuration item as synchronized with persistent storage.
     * <p>
     * After calling this method, {@link #hasUnsavedChanges()} should return {@code false} until the current value changes again.
     * </p>
     *
     * @since 1.0.0
     */
    void clearUnsavedChanges();
}