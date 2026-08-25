package eu.oberon.oss.tools.configitems.items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

import static eu.oberon.oss.tools.configitems.ConfigItems.PARAMETER_MUST_NOT_BE_NULL;

/**
 * A default implementation of {@link ConfigurationItem}.
 *
 * @param <I> The internal key type.
 * @param <A> The application data type.
 * @param <P> The storage data type.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public final class DefaultConfigurationItem<I, A, P> implements ConfigurationItem<I, A, P> {

    private final I itemID;
    private final A defaultValue;
    private final Class<A> applicationDataType;
    private final Class<P> storageType;
    private final Function<P, A> toDataType;
    private final Function<A, P> toStorageType;
    private A currentValue;
    private boolean changed;

    /**
     * Creates a new {@link ConfigurationItem}.
     *
     * @param itemID              The item ID for this configuration item.
     * @param defaultValue        The default value.
     * @param applicationDataType The application data type.
     * @param storageType         The storage data type.
     * @param toDataType          The function used to convert a storage value to an application value.
     * @param toStorageType       The function used to convert an application value to a storage value.
     *
     * @since 1.0.0
     */
    public DefaultConfigurationItem(
            I itemID,
            @Nullable A defaultValue,
            Class<A> applicationDataType,
            Class<P> storageType,
            Function<P, A> toDataType,
            Function<A, P> toStorageType
    ) {
        this.itemID = Objects.requireNonNull(itemID, PARAMETER_MUST_NOT_BE_NULL.getMessage("itemID"));
        this.applicationDataType = Objects.requireNonNull(applicationDataType, PARAMETER_MUST_NOT_BE_NULL.getMessage("applicationDataType"));
        this.storageType = Objects.requireNonNull(storageType, PARAMETER_MUST_NOT_BE_NULL.getMessage("storageType"));
        this.toDataType = Objects.requireNonNull(toDataType, PARAMETER_MUST_NOT_BE_NULL.getMessage("toDataType"));
        this.toStorageType = Objects.requireNonNull(toStorageType, PARAMETER_MUST_NOT_BE_NULL.getMessage("toStorageType"));

        this.currentValue = defaultValue;
        this.defaultValue = defaultValue;

        this.changed = false;
    }

    @Override
    public @NotNull I getKey() {
        return itemID;
    }

    @Override
    public void setCurrentValue(A value) {
        if (!Objects.equals(currentValue, value)) {
            currentValue = value;
            changed = true;
        }
    }

    @Override
    public @Nullable A getCurrentValue() {
        return currentValue;
    }

    @Override
    public @Nullable A getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean hasUnsavedChanges() {
        return changed;
    }

    @Override
    public void clearUnsavedChanges() {
        changed = false;
    }

    @Override
    public Class<A> applicationDataType() {
        return applicationDataType;
    }

    @Override
    public Class<P> storageType() {
        return storageType;
    }

    @Override
    public Function<P, A> toDataType() {
        return toDataType;
    }

    @Override
    public Function<A, P> toStorageType() {
        return toStorageType;
    }
}
