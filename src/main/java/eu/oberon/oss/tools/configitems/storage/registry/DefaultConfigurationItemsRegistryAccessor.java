package eu.oberon.oss.tools.configitems.storage.registry;

import eu.oberon.oss.tools.configitems.storage.ConfigurationItemKey;
import eu.oberon.oss.tools.configitems.storage.RegisteredConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.StorableConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.registry.listeners.ConfigurationItemRegistryEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static eu.oberon.oss.tools.configitems.ConfigItems.PARAMETER_MUST_NOT_BE_NULL;
import static eu.oberon.oss.tools.configitems.storage.registry.DefaultStorableConfigurationItemsRegistry.*;
import static eu.oberon.oss.tools.configitems.storage.registry.listeners.ConfigurationItemRegistryEventType.*;

/**
 * Default restricted accessor for a {@link DefaultStorableConfigurationItemsRegistry}.
 * <p>
 * This class intentionally implements only {@link ConfigurationItemsRegistryAccessor}. It delegates to the backing registry without exposing registry
 * management operations.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
final class DefaultConfigurationItemsRegistryAccessor implements ConfigurationItemsRegistryAccessor {
    private final DefaultStorableConfigurationItemsRegistry registry;

    DefaultConfigurationItemsRegistryAccessor(DefaultStorableConfigurationItemsRegistry registry) {
        this.registry = Objects.requireNonNull(registry, PARAMETER_MUST_NOT_BE_NULL.getMessage("registry"));
    }

    @Override
    public <A> @Nullable A getCurrentValue(ConfigurationItemKey<A> key) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));

        StorableConfigurationItem<Object, Object, A, Object> item = registry.getItem(key);

        if (item == null) {
            return null;
        }

        Object currentValue = item.getCurrentValue();
        return currentValue == null ? null : key.valueType().cast(currentValue);
    }

    @Override
    public <A> @NotNull Optional<A> findCurrentValue(ConfigurationItemKey<A> key) {
        return Optional.ofNullable(getCurrentValue(key));
    }

    @Override
    public <A> @Nullable A getRequiredCurrentValue(ConfigurationItemKey<A> key) {
        StorableConfigurationItem<Object, Object, A, Object> item = registry.getRequiredItem(key);

        Object currentValue = item.getCurrentValue();
        return currentValue == null ? null : key.valueType().cast(currentValue);
    }

    @Override
    public <A> boolean setCurrentValue(ConfigurationItemKey<A> key, @Nullable A currentValue) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));

        if (currentValue != null) {
            key.valueType().cast(currentValue);
        }

        StorableConfigurationItem<Object, Object, A, Object> item = registry.getItem(key);

        if (item == null) {
            return false;
        }

        Object previousValue = item.getCurrentValue();
        item.setCurrentValue(currentValue);

        if (!Objects.equals(previousValue, currentValue)) {
            registry.notifyListeners(new ConfigurationItemRegistryEvent(VALUE_CHANGED, key.id(), item, null));
        }
        return true;
    }

    @Override
    public <A> void setRequiredCurrentValue(ConfigurationItemKey<A> key, @Nullable A currentValue) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));

        if (currentValue != null) {
            key.valueType().cast(currentValue);
        }

        StorableConfigurationItem<Object, Object, A, Object> item = registry.getRequiredItem(key);
        Object previousValue = item.getCurrentValue();

        item.setCurrentValue(currentValue);

        if (!Objects.equals(previousValue, currentValue)) {
            registry.notifyListeners(new ConfigurationItemRegistryEvent(VALUE_CHANGED, key.id(), item, null));
        }
    }

    @Override
    public <A> boolean hasUnsavedChanges(ConfigurationItemKey<A> key) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));

        StorableConfigurationItem<Object, Object, A, Object> item = registry.getItem(key);

        return item != null && item.hasUnsavedChanges();
    }

    @Override
    public <A> boolean hasRequiredUnsavedChanges(ConfigurationItemKey<A> key) {
        Objects.requireNonNull(key, PARAMETER_MUST_NOT_BE_NULL.getMessage(PARAMETER_KEY));

        StorableConfigurationItem<Object, Object, A, Object> item = registry.getRequiredItem(key);

        return item.hasUnsavedChanges();
    }

    @Override
    public void loadItems() {
        for (RegisteredConfigurationItem item : registry.registeredItemsSnapshot()) {
            item.load();
            registry.notifyListeners(new ConfigurationItemRegistryEvent(LOADED, item.getKey(), item, null));
        }
    }

    @Override
    public void loadItems(Object... itemIds) {
        for (RegisteredConfigurationItem item : getRequiredItemsSnapshot(itemIds)) {
            item.load();
            registry.notifyListeners(new ConfigurationItemRegistryEvent(LOADED, item.getKey(), item, null));
        }
    }

    @Override
    public void loadItemsByKey(ConfigurationItemKey<?>... keys) {
        for (RegisteredConfigurationItem item : getRequiredItemsSnapshotByKey(keys)) {
            item.load();
            registry.notifyListeners(new ConfigurationItemRegistryEvent(LOADED, item.getKey(), item, null));
        }
    }

    @Override
    public void saveItems() {
        for (RegisteredConfigurationItem item : registry.registeredItemsSnapshot()) {
            item.save();
            registry.notifyListeners(new ConfigurationItemRegistryEvent(SAVED, item.getKey(), item, null));
        }
    }

    @Override
    public void saveItems(Object... itemIds) {
        for (RegisteredConfigurationItem item : getRequiredItemsSnapshot(itemIds)) {

            item.save();
            registry.notifyListeners(new ConfigurationItemRegistryEvent(SAVED, item.getKey(), item, null));
        }
    }

    @Override
    public void saveItemsByKey(ConfigurationItemKey<?>... keys) {
        for (RegisteredConfigurationItem item : getRequiredItemsSnapshotByKey(keys)) {
            item.save();
            registry.notifyListeners(new ConfigurationItemRegistryEvent(SAVED, item.getKey(), item, null));
        }
    }

    private @NotNull List<RegisteredConfigurationItem> getRequiredItemsSnapshot(Object... itemIds) {
        return getRequiredItemsSnapshot(itemIds, PARAMETER_ITEM_IDS, PARAMETER_ITEM_ID, Function.identity()
        );
    }

    private @NotNull List<RegisteredConfigurationItem> getRequiredItemsSnapshotByKey(ConfigurationItemKey<?>... keys) {
        return getRequiredItemsSnapshot(keys, PARAMETER_KEYS, PARAMETER_KEY, ConfigurationItemKey::id);
    }

    private <T> @NotNull List<RegisteredConfigurationItem> getRequiredItemsSnapshot(
            T[] elements,
            String parameterName,
            String elementName,
            Function<T, Object> toItemId
    ) {
        Objects.requireNonNull(elements, PARAMETER_MUST_NOT_BE_NULL.getMessage(parameterName));

        List<RegisteredConfigurationItem> items = new ArrayList<>(elements.length);

        for (T element : elements) {
            Objects.requireNonNull(element, PARAMETER_MUST_NOT_BE_NULL.getMessage(elementName));

            Object itemId = toItemId.apply(element);
            RegisteredConfigurationItem item = registry.getRequiredRegisteredItem(itemId);

            items.add(item);
        }

        return List.copyOf(items);
    }
}