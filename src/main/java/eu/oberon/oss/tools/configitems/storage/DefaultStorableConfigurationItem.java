package eu.oberon.oss.tools.configitems.storage;

import eu.oberon.oss.tools.configitems.items.ConfigurationItem;
import eu.oberon.oss.tools.configitems.items.ConfigurationItemAccessor;
import eu.oberon.oss.tools.configitems.storage.providers.StorageProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of {@link StorableConfigurationItem}.
 *
 * @param configurationItem         The basic configuration item that holds the data for a configuration item as used by an application
 * @param configurationItemAccessor Allows access to converters.
 * @param storageProvider           Allows access to the storage provider.
 * @param <I>                       the internal key type
 * @param <K>                       the external key type
 * @param <A>                       the data type as used by an application
 * @param <P>                       the type of class the 'applicationDataType' is stored by the {@link StorageProvider}
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record DefaultStorableConfigurationItem<I, K, A, P>(ConfigurationItem<I, A> configurationItem,
                                                           ConfigurationItemAccessor<I, K, A, P> configurationItemAccessor,
                                                           StorageProvider storageProvider) implements StorableConfigurationItem<I, K, A, P> {
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
    public boolean hasUnsavedChanges() {
        return configurationItem.hasUnsavedChanges();
    }

    @Override
    public void clearUnsavedChanges() {
        configurationItem.clearUnsavedChanges();
    }
}