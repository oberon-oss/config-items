package eu.oberon.oss.tools.configitems.storage;

import eu.oberon.oss.tools.configitems.items.ConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.providers.StorageProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Default implementation of {@link StorableConfigurationItem}.
 *
 * @param configurationItem The basic configuration item that holds the data for a configuration item as used by an application.
 * @param intKeyType        The internal key type.
 * @param extKeyType        The external key type.
 * @param toExtKey          Function to convert an internal key representation {@code <I>} to its external {@code <K>} form.
 * @param storageProvider   Allows access to the storage provider.
 * @param <I>               the internal key type
 * @param <K>               the external key type
 * @param <A>               the data type as used by an application
 * @param <P>               the type of class the application data type is stored by the {@link StorageProvider}
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record DefaultStorableConfigurationItem<I, K, A, P>(
        ConfigurationItem<I, A, P> configurationItem,
        Class<I> intKeyType,
        Class<K> extKeyType,
        Function<I, K> toExtKey,
        StorageProvider storageProvider
) implements StorableConfigurationItem<I, K, A, P> {
    @Override
    public @NotNull I getKey() {
        return configurationItem.getKey();
    }

    @Override
    public void setCurrentValue(A value) {
        configurationItem.setCurrentValue(value);
    }

    @Override
    public @Nullable A getCurrentValue() {
        return configurationItem.getCurrentValue();
    }

    @Override
    public @Nullable A getDefaultValue() {
        return configurationItem.getDefaultValue();
    }

    @Override
    public Class<A> applicationDataType() {
        return configurationItem.applicationDataType();
    }

    @Override
    public Class<P> storageType() {
        return configurationItem.storageType();
    }

    @Override
    public Function<P, A> toDataType() {
        return configurationItem.toDataType();
    }

    @Override
    public Function<A, P> toStorageType() {
        return configurationItem.toStorageType();
    }

    @Override
    public boolean hasUnsavedChanges() {
        return configurationItem.hasUnsavedChanges();
    }

    @Override
    public void clearUnsavedChanges() {
        configurationItem.clearUnsavedChanges();
    }
}