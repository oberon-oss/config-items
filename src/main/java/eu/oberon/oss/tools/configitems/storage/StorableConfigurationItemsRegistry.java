package eu.oberon.oss.tools.configitems.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for {@link StorableConfigurationItem} instances.
 * <p>
 * The registry is thread-safe for registration, lookup and removal operations. The contained configuration items themselves are not synchronized by this class.
 * If the same item is mutated, loaded or saved concurrently, thread-safety depends on the item and its {@link StorageProvider}.
 * </p>
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class StorableConfigurationItemsRegistry {

    private final Map<Object, RegisteredConfigurationItem> storableConfigurationItems = new ConcurrentHashMap<>();

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
    public <I> void register(@NotNull StorableConfigurationItem<I, ?, ?, ?> storableConfigurationItem) {
        Objects.requireNonNull(storableConfigurationItem, "Parameter: storableConfigurationItem");

        I key = Objects.requireNonNull(storableConfigurationItem.getKey(), "Configuration item key must not be null");
        RegisteredConfigurationItem previousItem = storableConfigurationItems.putIfAbsent(key, storableConfigurationItem);

        if (previousItem != null) {
            throw new IllegalArgumentException("A configuration item with key '" + key + "' is already registered");
        }
    }

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
    public <I> @Nullable RegisteredConfigurationItem replace(@NotNull StorableConfigurationItem<I, ?, ?, ?> storableConfigurationItem) {
        Objects.requireNonNull(storableConfigurationItem, "Parameter: storableConfigurationItem");

        I key = Objects.requireNonNull(storableConfigurationItem.getKey(), "Configuration item key must not be null");
        return storableConfigurationItems.put(key, storableConfigurationItem);
    }

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
    @SuppressWarnings("unchecked")
    public <K, A, P> @Nullable StorableConfigurationItem<Object, K, A, P> getItem(@NotNull ConfigurationItemKey<A> key) {
        Objects.requireNonNull(key, "Parameter: key");
        return (StorableConfigurationItem<Object, K, A, P>) storableConfigurationItems.get(key.id());
    }

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
    public @Nullable RegisteredConfigurationItem getItem(@NotNull Object itemId) {
        Objects.requireNonNull(itemId, "Parameter: itemId");
        return storableConfigurationItems.get(itemId);
    }

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
    public <K, A, P> @NotNull Optional<StorableConfigurationItem<Object, K, A, P>> findItem(@NotNull ConfigurationItemKey<A> key) {
        return Optional.ofNullable(getItem(key));
    }

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
    public <K, A, P> @NotNull StorableConfigurationItem<Object, K, A, P> getRequiredItem(@NotNull ConfigurationItemKey<A> key) {
        StorableConfigurationItem<Object, K, A, P> item = getItem(key);

        if (item == null) {
            throw new NoSuchElementException("No configuration item registered for key '" + key.id() + "'");
        }

        return item;
    }

    /**
     * Retrieves the current value of a registered item.
     *
     * @param key The typed key of the item.
     * @param <A> The application-level value type.
     *
     * @return The current value, or {@code null} if no item exists for the key or the current value is {@code null}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws ClassCastException   if the current value is not assignable to the key's value type.
     * @since 1.0.0
     */
    public <A> @Nullable A getCurrentValue(@NotNull ConfigurationItemKey<A> key) {
        Objects.requireNonNull(key, "Parameter: key");

        StorableConfigurationItem<Object, Object, A, Object> item = getItem(key);

        if (item == null) {
            return null;
        }

        Object currentValue = item.getCurrentValue();
        return currentValue == null ? null : key.valueType().cast(currentValue);
    }

    /**
     * Retrieves the current value of a registered item.
     *
     * @param key The typed key of the item.
     * @param <A> The application-level value type.
     *
     * @return An {@link Optional} containing the current value, or an empty {@link Optional} if no item exists for the key or the current value is {@code null}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws ClassCastException   if the current value is not assignable to the key's value type.
     * @since 1.0.0
     */
    public <A> @NotNull Optional<A> findCurrentValue(@NotNull ConfigurationItemKey<A> key) {
        return Optional.ofNullable(getCurrentValue(key));
    }

    /**
     * Retrieves the current value of a registered item.
     *
     * @param key The typed key of the item.
     * @param <A> The application-level value type.
     *
     * @return The current value. This may be {@code null} if the registered item currently has a {@code null} value.
     *
     * @throws NullPointerException   if {@code key} is {@code null}.
     * @throws NoSuchElementException if no item exists for the key.
     * @throws ClassCastException     if the current value is not assignable to the key's value type.
     * @since 1.0.0
     */
    public <A> @Nullable A getRequiredCurrentValue(@NotNull ConfigurationItemKey<A> key) {
        StorableConfigurationItem<Object, Object, A, Object> item = getRequiredItem(key);

        Object currentValue = item.getCurrentValue();
        return currentValue == null ? null : key.valueType().cast(currentValue);
    }

    /**
     * Sets the current value of a registered item.
     * <p>
     * A {@code null} value is allowed and is delegated to the registered item.
     * </p>
     *
     * @param key          The typed key of the item.
     * @param currentValue The new current value.
     * @param <A>          The application-level value type.
     *
     * @return {@code true} if the item was found and updated, otherwise {@code false}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws ClassCastException   if {@code currentValue} is not assignable to the key's value type.
     * @since 1.0.0
     */
    public <A> boolean setCurrentValue(@NotNull ConfigurationItemKey<A> key, @Nullable A currentValue) {
        Objects.requireNonNull(key, "Parameter: key");

        if (currentValue != null) {
            key.valueType().cast(currentValue);
        }

        StorableConfigurationItem<Object, Object, A, Object> item = getItem(key);

        if (item == null) {
            return false;
        }

        item.setCurrentValue(currentValue);
        return true;
    }

    /**
     * Sets the current value of a registered item.
     *
     * @param key          The typed key of the item.
     * @param currentValue The new current value.
     * @param <A>          The application-level value type.
     *
     * @throws NullPointerException   if {@code key} is {@code null}.
     * @throws NoSuchElementException if no item exists for the key.
     * @throws ClassCastException     if {@code currentValue} is not assignable to the key's value type.
     * @since 1.0.0
     */
    public <A> void setRequiredCurrentValue(@NotNull ConfigurationItemKey<A> key, @Nullable A currentValue) {
        Objects.requireNonNull(key, "Parameter: key");

        if (currentValue != null) {
            key.valueType().cast(currentValue);
        }

        StorableConfigurationItem<Object, Object, A, Object> item = getRequiredItem(key);
        item.setCurrentValue(currentValue);
    }

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
    public boolean containsItem(@NotNull ConfigurationItemKey<?> key) {
        Objects.requireNonNull(key, "Parameter: key");
        return storableConfigurationItems.containsKey(key.id());
    }

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
    public boolean containsItemId(@NotNull Object itemId) {
        Objects.requireNonNull(itemId, "Parameter: itemId");
        return storableConfigurationItems.containsKey(itemId);
    }

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
    public @Nullable RegisteredConfigurationItem unregister(@NotNull ConfigurationItemKey<?> key) {
        Objects.requireNonNull(key, "Parameter: key");
        return storableConfigurationItems.remove(key.id());
    }

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
    public @Nullable RegisteredConfigurationItem unregisterById(@NotNull Object itemId) {
        Objects.requireNonNull(itemId, "Parameter: itemId");
        return storableConfigurationItems.remove(itemId);
    }

    /**
     * Removes all registered items.
     *
     * @since 1.0.0
     */
    public void clear() {
        storableConfigurationItems.clear();
    }

    /**
     * Returns the number of registered items.
     *
     * @return The number of registered items.
     *
     * @since 1.0.0
     */
    public int size() {
        return storableConfigurationItems.size();
    }

    /**
     * Loads all currently registered items.
     * <p>
     * This method operates on a stable snapshot of the registry. Items registered while this method is running are not guaranteed to be loaded by this invocation.
     * </p>
     *
     * @since 1.0.0
     */
    public void loadItems() {
        for (RegisteredConfigurationItem item : List.copyOf(storableConfigurationItems.values())) {
            item.load();
        }
    }

    /**
     * Saves all currently registered items.
     * <p>
     * This method operates on a stable snapshot of the registry. Items registered while this method is running are not guaranteed to be saved by this invocation.
     * </p>
     *
     * @since 1.0.0
     */
    public void saveItems() {
        for (RegisteredConfigurationItem item : List.copyOf(storableConfigurationItems.values())) {
            item.save();
        }
    }
}