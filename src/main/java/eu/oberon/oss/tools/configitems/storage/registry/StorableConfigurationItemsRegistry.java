package eu.oberon.oss.tools.configitems.storage.registry;

import eu.oberon.oss.tools.configitems.storage.ConfigurationItemKey;
import eu.oberon.oss.tools.configitems.storage.RegisteredConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.StorableConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.registry.listeners.ConfigurationItemRegistryListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * An interface for managing a registry of storable configuration items. Provides methods for registering, retrieving, updating, and removing configuration
 * items.
 * <p>
 * The registry enforces unique keys for the registered items and offers both key-based and id-based lookup capabilities. It supports operations such as
 * replacing existing items, clearing the entire registry, and querying items using various criteria.
 * <p>
 * Generic type parameters are used to ensure type safety when dealing with configuration items.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface StorableConfigurationItemsRegistry {
    /**
     * Registers a storable configuration item.
     * <p>
     * Duplicate keys are rejected. Use {@link #replace(StorableConfigurationItem)} when replacement is intended.
     * </p>
     *
     * @param storableConfigurationItem The configuration item to register.
     * @param <I>                       The type of the item key.
     *
     * @throws NullPointerException     if {@code storableConfigurationItem} or its key is {@code null}.
     * @throws IllegalArgumentException if an item with the same key is already registered.
     * @since 1.0.0
     */
    <I> void register(StorableConfigurationItem<I, ?, ?, ?> storableConfigurationItem);

    /**
     * Replaces an existing item or registers it when no item with the same key exists.
     *
     * @param storableConfigurationItem The configuration item to register or replace.
     * @param <I>                       The type of the item key.
     *
     * @return The previously registered item, or {@code null} if no item was registered for the key.
     *
     * @throws NullPointerException if {@code storableConfigurationItem} or its key is {@code null}.
     * @since 1.0.0
     */
    <I> @Nullable RegisteredConfigurationItem replace(StorableConfigurationItem<I, ?, ?, ?> storableConfigurationItem);

    /**
     * Retrieves a registered configuration item using a typed key.
     *
     * @param key The typed key of the item to retrieve.
     * @param <K> The external key type.
     * @param <A> The application-level value type.
     * @param <P> The storage value type.
     *
     * @return The registered item, or {@code null} if no item exists for the key.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @since 1.0.0
     */
    <K, A, P> @Nullable StorableConfigurationItem<Object, K, A, P> getItem(ConfigurationItemKey<A> key);

    /**
     * Retrieves a registered configuration item using a raw item id.
     * <p>
     * Prefer {@link #getItem(ConfigurationItemKey)} when the value type matters.
     * </p>
     *
     * @param itemId The raw item id.
     *
     * @return The registered item, or {@code null} if no item exists for the id.
     *
     * @throws NullPointerException if {@code itemId} is {@code null}.
     * @since 1.0.0
     */
    @Nullable RegisteredConfigurationItem getItem(Object itemId);

    /**
     * Retrieves a registered configuration item using a typed key.
     *
     * @param key The typed key of the item to retrieve.
     * @param <K> The external key type.
     * @param <A> The application-level value type.
     * @param <P> The storage value type.
     *
     * @return An {@link Optional} containing the registered item, or an empty {@link Optional} if no item exists for the key.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @since 1.0.0
     */
    <K, A, P> @NotNull Optional<StorableConfigurationItem<Object, K, A, P>> findItem(ConfigurationItemKey<A> key);

    /**
     * Retrieves a registered configuration item using a typed key.
     *
     * @param key The typed key of the item to retrieve.
     * @param <K> The external key type.
     * @param <A> The application-level value type.
     * @param <P> The storage value type.
     *
     * @return The registered item.
     *
     * @throws NullPointerException   if {@code key} is {@code null}.
     * @throws NoSuchElementException if no item exists for the key.
     * @since 1.0.0
     */
    <K, A, P> @NotNull StorableConfigurationItem<Object, K, A, P> getRequiredItem(ConfigurationItemKey<A> key);

    /**
     * Checks whether an item is registered for the provided typed key.
     *
     * @param key The typed key to check.
     *
     * @return {@code true} if an item is registered for the key, otherwise {@code false}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @since 1.0.0
     */
    boolean containsItem(ConfigurationItemKey<?> key);

    /**
     * Checks whether an item is registered for the provided raw item id.
     *
     * @param itemId The raw item id to check.
     *
     * @return {@code true} if an item is registered for the id, otherwise {@code false}.
     *
     * @throws NullPointerException if {@code itemId} is {@code null}.
     * @since 1.0.0
     */
    boolean containsItemId(Object itemId);

    /**
     * Unregisters the item associated with the provided typed key.
     *
     * @param key The typed key of the item to unregister.
     *
     * @return The removed item, or {@code null} if no item existed for the key.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @since 1.0.0
     */
    @Nullable RegisteredConfigurationItem unregister(ConfigurationItemKey<?> key);

    /**
     * Unregisters the item associated with the provided raw item id.
     *
     * @param itemId The raw item id of the item to unregister.
     *
     * @return The removed item, or {@code null} if no item existed for the id.
     *
     * @throws NullPointerException if {@code itemId} is {@code null}.
     * @since 1.0.0
     */
    @Nullable RegisteredConfigurationItem unregisterById(Object itemId);

    /**
     * Removes all registered items.
     *
     * @since 1.0.0
     */
    void clear();

    /**
     * Returns the number of registered items.
     *
     * @return The number of registered items.
     *
     * @since 1.0.0
     */
    int size();

    /**
     * Returns the accessor for this registry.
     *
     * @return The accessor.
     *
     * @since 1.0.0
     */
    ConfigurationItemsRegistryAccessor getAccessor();

    /**
     * Registers a listener that will be notified when registry events occur.
     *
     * @param listener The listener to register.
     *
     * @throws NullPointerException if {@code listener} is {@code null}.
     * @since 1.0.0
     */
    void addListener(ConfigurationItemRegistryListener listener);

    /**
     * Removes a previously registered listener.
     *
     * @param listener The listener to remove.
     *
     * @return {@code true} if the listener was registered and removed, otherwise {@code false}.
     *
     * @throws NullPointerException if {@code listener} is {@code null}.
     * @since 1.0.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean removeListener(ConfigurationItemRegistryListener listener);

}
