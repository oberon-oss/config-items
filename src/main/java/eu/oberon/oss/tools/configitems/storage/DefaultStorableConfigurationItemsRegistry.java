package eu.oberon.oss.tools.configitems.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static eu.oberon.oss.tools.configitems.ConfigItems.CONFIGURATION_ALREADY_DEFINED;
import static eu.oberon.oss.tools.configitems.ConfigItems.CONFIGURATION_ITEM_KEY_MUST_NOT_BE_NULL;
import static eu.oberon.oss.tools.configitems.ConfigItems.NO_CONFIGURATION_ITEM_REGISTERED;
import static eu.oberon.oss.tools.configitems.ConfigItems.PARAMETER_MUST_NOT_BE_NULL;

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
public class DefaultStorableConfigurationItemsRegistry implements StorableConfigurationItemsRegistry {
    static final String PARAMETER_ITEM_ID = "itemId";
    static final String PARAMETER_ITEM_IDS = "itemIds";
    static final String PARAMETER_KEY = "key";
    static final String PARAMETER_KEYS = "keys";
    static final String PARAMETER_STORABLE_CONFIGURATION_ITEM = "storableConfigurationItem";

    private final Map<Object, RegisteredConfigurationItem> storableConfigurationItems;
    private final ConfigurationItemsRegistryAccessor accessor;

    /**
     * Default constructor.
     */
    public DefaultStorableConfigurationItemsRegistry() {
        storableConfigurationItems = new ConcurrentHashMap<>();
        accessor = new DefaultConfigurationItemsRegistryAccessor(this);
    }

    Collection<RegisteredConfigurationItem> registeredItemsSnapshot() {
        return List.copyOf(storableConfigurationItems.values());
    }

    RegisteredConfigurationItem getRequiredRegisteredItem(Object itemId) {
        Objects.requireNonNull(itemId, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_ITEM_ID));

        RegisteredConfigurationItem item = storableConfigurationItems.get(itemId);

        if (item == null) {
            throw NO_CONFIGURATION_ITEM_REGISTERED.getException(NoSuchElementException.class, itemId);
        }

        return item;
    }

    @Override
    public <I> void register(StorableConfigurationItem<I, ?, ?, ?> storableConfigurationItem) {
        Objects.requireNonNull(storableConfigurationItem, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_STORABLE_CONFIGURATION_ITEM));

        I key = Objects.requireNonNull(storableConfigurationItem.getKey(), CONFIGURATION_ITEM_KEY_MUST_NOT_BE_NULL.getMessage());
        RegisteredConfigurationItem previousItem = storableConfigurationItems.putIfAbsent(key, storableConfigurationItem);

        if (previousItem != null) {
            throw CONFIGURATION_ALREADY_DEFINED.getException(IllegalArgumentException.class, key);
        }
    }

    @Override
    public <I> @Nullable RegisteredConfigurationItem replace(StorableConfigurationItem<I, ?, ?, ?> storableConfigurationItem) {
        Objects.requireNonNull(storableConfigurationItem, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_STORABLE_CONFIGURATION_ITEM));

        I key = Objects.requireNonNull(storableConfigurationItem.getKey(), CONFIGURATION_ITEM_KEY_MUST_NOT_BE_NULL.getMessage());
        return storableConfigurationItems.put(key, storableConfigurationItem);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, A, P> @Nullable StorableConfigurationItem<Object, K, A, P> getItem(ConfigurationItemKey<A> key) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));
        return (StorableConfigurationItem<Object, K, A, P>) storableConfigurationItems.get(key.id());
    }

    @Override
    public @Nullable RegisteredConfigurationItem getItem(Object itemId) {
        Objects.requireNonNull(itemId, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_ITEM_ID));
        return storableConfigurationItems.get(itemId);
    }

    @Override
    public <K, A, P> @NotNull Optional<StorableConfigurationItem<Object, K, A, P>> findItem(ConfigurationItemKey<A> key) {
        return Optional.ofNullable(getItem(key));
    }

    @Override
    public <K, A, P> @NotNull StorableConfigurationItem<Object, K, A, P> getRequiredItem(ConfigurationItemKey<A> key) {
        StorableConfigurationItem<Object, K, A, P> item = getItem(key);

        if (item == null) {
            throw NO_CONFIGURATION_ITEM_REGISTERED.getException(NoSuchElementException.class, key.id());
        }

        return item;
    }

    @Override
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

    @Override
    public <A> void setRequiredCurrentValue(ConfigurationItemKey<A> key, @Nullable A currentValue) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));

        if (currentValue != null) {
            key.valueType().cast(currentValue);
        }

        StorableConfigurationItem<Object, Object, A, Object> item = getRequiredItem(key);
        item.setCurrentValue(currentValue);
    }

    @Override
    public boolean containsItem(ConfigurationItemKey<?> key) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));
        return storableConfigurationItems.containsKey(key.id());
    }

    @Override
    public boolean containsItemId(Object itemId) {
        Objects.requireNonNull(itemId, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_ITEM_ID));
        return storableConfigurationItems.containsKey(itemId);
    }

    @Override
    public @Nullable RegisteredConfigurationItem unregister(ConfigurationItemKey<?> key) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));
        return storableConfigurationItems.remove(key.id());
    }

    @Override
    public @Nullable RegisteredConfigurationItem unregisterById(Object itemId) {
        Objects.requireNonNull(itemId, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_ITEM_ID));
        return storableConfigurationItems.remove(itemId);
    }

    @Override
    public void clear() {
        storableConfigurationItems.clear();
    }

    @Override
    public int size() {
        return storableConfigurationItems.size();
    }

    @Override
    public ConfigurationItemsRegistryAccessor getAccessor() {
        return accessor;
    }
}