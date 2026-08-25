package eu.oberon.oss.tools.configitems.storage.registry;

import eu.oberon.oss.tools.configitems.storage.ConfigurationItemKey;
import eu.oberon.oss.tools.configitems.storage.RegisteredConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.StorableConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.providers.StorageProvider;
import eu.oberon.oss.tools.configitems.storage.registry.listeners.ConfigurationItemRegistryEvent;
import eu.oberon.oss.tools.configitems.storage.registry.listeners.ConfigurationItemRegistryListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static eu.oberon.oss.tools.configitems.ConfigItems.*;
import static eu.oberon.oss.tools.configitems.storage.registry.listeners.ConfigurationItemRegistryEventType.*;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStorableConfigurationItemsRegistry.class);

    static final String PARAMETER_ITEM_ID = "itemId";
    static final String PARAMETER_ITEM_IDS = "itemIds";
    static final String PARAMETER_KEY = "key";
    static final String PARAMETER_KEYS = "keys";
    static final String PARAMETER_STORABLE_CONFIGURATION_ITEM = "storableConfigurationItem";

    private final Map<Object, RegisteredConfigurationItem> storableConfigurationItems;
    private final ConfigurationItemsRegistryAccessor accessor;
    private final List<ConfigurationItemRegistryListener> listeners;

    /**
     * Default constructor.
     */
    public DefaultStorableConfigurationItemsRegistry() {
        storableConfigurationItems = new ConcurrentHashMap<>();
        listeners = new CopyOnWriteArrayList<>();
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

        notifyListeners(new ConfigurationItemRegistryEvent(REGISTERED, key, storableConfigurationItem, null));
    }

    @Override
    public <I> @Nullable RegisteredConfigurationItem replace(StorableConfigurationItem<I, ?, ?, ?> storableConfigurationItem) {
        Objects.requireNonNull(storableConfigurationItem, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_STORABLE_CONFIGURATION_ITEM));

        I key = Objects.requireNonNull(storableConfigurationItem.getKey(), CONFIGURATION_ITEM_KEY_MUST_NOT_BE_NULL.getMessage());

        RegisteredConfigurationItem previousItem = storableConfigurationItems.put(key, storableConfigurationItem);
        notifyListeners(new ConfigurationItemRegistryEvent(REPLACED, key, storableConfigurationItem, previousItem));

        return previousItem;
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

        RegisteredConfigurationItem removedItem = storableConfigurationItems.remove(key.id());
        if (removedItem != null) {
            notifyListeners(new ConfigurationItemRegistryEvent(UNREGISTERED, key.id(), removedItem, null));
        }

        return removedItem;
    }

    @Override
    public @Nullable RegisteredConfigurationItem unregisterById(Object itemId) {
        Objects.requireNonNull(itemId, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_ITEM_ID));

        RegisteredConfigurationItem removedItem = storableConfigurationItems.remove(itemId);
        if (removedItem != null) {
            notifyListeners(new ConfigurationItemRegistryEvent(UNREGISTERED, itemId, removedItem, null));
        }

        return removedItem;
    }

    @Override
    public void clear() {
        if (storableConfigurationItems.isEmpty()) {
            return;
        }
        storableConfigurationItems.clear();
        notifyListeners(new ConfigurationItemRegistryEvent(CLEARED, null, null, null));
    }

    @Override
    public int size() {
        return storableConfigurationItems.size();
    }

    @Override
    public ConfigurationItemsRegistryAccessor getAccessor() {
        return accessor;
    }

    @Override
    public void addListener(ConfigurationItemRegistryListener listener) {
        listeners.add(Objects.requireNonNull(listener, PARAMETER_MUST_NOT_BE_NULL.getMessage("listener")));
    }

    @Override
    public boolean removeListener(ConfigurationItemRegistryListener listener) {
        return listeners.remove(Objects.requireNonNull(listener, PARAMETER_MUST_NOT_BE_NULL.getMessage("listener")));
    }

    void notifyListeners(ConfigurationItemRegistryEvent event) {
        for (ConfigurationItemRegistryListener listener : listeners) {
            try {
                listener.onRegistryEvent(event);
            } catch (RuntimeException e) {
                LOGGER.error("Error while notifying listener: {}", e.getMessage(), e);
            }
        }
    }
}