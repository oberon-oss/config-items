package eu.oberon.oss.tools.configitems.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static eu.oberon.oss.tools.configitems.ConfigItems.*;

/**
 * Registry for {@link StorableConfigurationItem} instances.
 * <p>
 * The registry is thread-safe for registration, lookup, and removal operations.
 * <p>
 * This class does not synchronize the contained configuration items themselves. If the same item is mutated, loaded, or saved concurrently, thread-safety
 * depends on the item and its {@link StorageProvider}.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class StorableConfigurationItemsRegistry {
    private static final String PARAMETER_ITEM_ID = "itemId";
    private static final String PARAMETER_ITEM_IDS = "itemIds";
    private static final String PARAMETER_KEY = "key";
    private static final String PARAMETER_STORABLE_CONFIGURATION_ITEM = "storableConfigurationItem";

    private final Map<Object, RegisteredConfigurationItem> storableConfigurationItems;

    /**
     * Default constructor.
     */
    public StorableConfigurationItemsRegistry() {
        storableConfigurationItems = new ConcurrentHashMap<>();
    }

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
    public <I> void register(StorableConfigurationItem<I, ?, ?, ?> storableConfigurationItem) {
        Objects.requireNonNull(storableConfigurationItem, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_STORABLE_CONFIGURATION_ITEM));

        I key = Objects.requireNonNull(storableConfigurationItem.getKey(), CONFIGURATION_ITEM_KEY_MUST_NOT_BE_NULL.getMessage());
        RegisteredConfigurationItem previousItem = storableConfigurationItems.putIfAbsent(key, storableConfigurationItem);

        if (previousItem != null) {
            throw CONFIGURATION_ALREADY_DEFINED.getException(IllegalArgumentException.class, key);
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
    public <I> @Nullable RegisteredConfigurationItem replace(StorableConfigurationItem<I, ?, ?, ?> storableConfigurationItem) {
        Objects.requireNonNull(storableConfigurationItem, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_STORABLE_CONFIGURATION_ITEM));

        I key = Objects.requireNonNull(storableConfigurationItem.getKey(), CONFIGURATION_ITEM_KEY_MUST_NOT_BE_NULL.getMessage());
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
    public <K, A, P> @Nullable StorableConfigurationItem<Object, K, A, P> getItem(ConfigurationItemKey<A> key) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));
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
    public @Nullable RegisteredConfigurationItem getItem(Object itemId) {
        Objects.requireNonNull(itemId, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_ITEM_ID));
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
    public <K, A, P> @NotNull Optional<StorableConfigurationItem<Object, K, A, P>> findItem(ConfigurationItemKey<A> key) {
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
    public <K, A, P> @NotNull StorableConfigurationItem<Object, K, A, P> getRequiredItem(ConfigurationItemKey<A> key) {
        StorableConfigurationItem<Object, K, A, P> item = getItem(key);

        if (item == null) {
            throw NO_CONFIGURATION_ITEM_REGISTERED.getException(NoSuchElementException.class, key.id());
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
    public <A> @Nullable A getCurrentValue(ConfigurationItemKey<A> key) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));

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
     * @return An {@link Optional} containing the current value, or an empty {@link Optional} if no item exists for the key or the current value is
     *         {@code null}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws ClassCastException   if the current value is not assignable to the key's value type.
     * @since 1.0.0
     */
    public <A> @NotNull Optional<A> findCurrentValue(ConfigurationItemKey<A> key) {
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
    public <A> @Nullable A getRequiredCurrentValue(ConfigurationItemKey<A> key) {
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
    public <A> boolean setCurrentValue(ConfigurationItemKey<A> key, @Nullable A currentValue) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));

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
    public <A> void setRequiredCurrentValue(ConfigurationItemKey<A> key, @Nullable A currentValue) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));

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
    public boolean containsItem(ConfigurationItemKey<?> key) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));
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
    public boolean containsItemId(Object itemId) {
        Objects.requireNonNull(itemId, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_ITEM_ID));
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
    public @Nullable RegisteredConfigurationItem unregister(ConfigurationItemKey<?> key) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));
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
    public @Nullable RegisteredConfigurationItem unregisterById(Object itemId) {
        Objects.requireNonNull(itemId, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_ITEM_ID));
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
     * This method operates on a stable snapshot of the registry. Items registered, removed, or replaced while this method is running are not guaranteed to
     * affect this invocation.
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
     * Loads the specified items.
     * <p>
     * This method operates on a stable snapshot of the selected registry entries. Items registered, removed, or replaced while this method is running are not
     * guaranteed to affect this invocation.
     * </p>
     *
     * @param itemIds The IDs of the items to load.
     *
     * @throws NullPointerException   if {@code itemIds} or one of its elements is {@code null}.
     * @throws NoSuchElementException if no item exists for one of the provided ids.
     * @since 1.0.0
     */
    public void loadItems(Object... itemIds) {
        for (RegisteredConfigurationItem item : getRequiredItemsSnapshot(itemIds)) {
            item.load();
        }
    }

    /**
     * Saves all currently registered items.
     * <p>
     * This method operates on a stable snapshot of the registry. Items registered, removed, or replaced while this method is running are not guaranteed to
     * affect this invocation.
     * </p>
     *
     * @since 1.0.0
     */
    public void saveItems() {
        for (RegisteredConfigurationItem item : List.copyOf(storableConfigurationItems.values())) {
            item.save();
        }
    }

    /**
     * Saves the specified items.
     * <p>
     * This method operates on a stable snapshot of the selected registry entries. Items registered, removed, or replaced while this method is running are not
     * guaranteed to affect this invocation.
     * </p>
     *
     * @param itemIds The IDs of the items to save.
     *
     * @throws NullPointerException   if {@code itemIds} or one of its elements is {@code null}.
     * @throws NoSuchElementException if no item exists for one of the provided ids.
     * @since 1.0.0
     */
    public void saveItems(Object... itemIds) {
        for (RegisteredConfigurationItem item : getRequiredItemsSnapshot(itemIds)) {
            item.save();
        }
    }

    private @NotNull List<RegisteredConfigurationItem> getRequiredItemsSnapshot(Object... itemIds) {
        Objects.requireNonNull(itemIds, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_ITEM_IDS));

        List<RegisteredConfigurationItem> items = new ArrayList<>(itemIds.length);

        for (Object itemId : itemIds) {
            Objects.requireNonNull(itemId, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_ITEM_ID));

            RegisteredConfigurationItem item = storableConfigurationItems.get(itemId);

            if (item == null) {
                throw NO_CONFIGURATION_ITEM_REGISTERED.getException(NoSuchElementException.class, itemId);
            }

            items.add(item);
        }

        return List.copyOf(items);
    }
}