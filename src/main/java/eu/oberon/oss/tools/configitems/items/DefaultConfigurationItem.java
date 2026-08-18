package eu.oberon.oss.tools.configitems.items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static eu.oberon.oss.tools.configitems.ConfigItems.PARAMETER_MUST_NOT_BE_NULL;

/**
 * A default implementation of {@link ConfigurationItem}.
 *
 * @param <I> The internal key type.
 * @param <A> The application data type.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public final class DefaultConfigurationItem<I, A> implements ConfigurationItem<I, A> {

    private final I itemID;
    private final A defaultValue;
    private A currentValue;

    /**
     * Creates a new {@link ConfigurationItem}.
     *
     * @param itemID       The item ID for this configuration item.
     * @param defaultValue The default value.
     *
     * @since 1.0.0
     */
    public DefaultConfigurationItem(I itemID, @Nullable A defaultValue) {
        this.itemID = Objects.requireNonNull(itemID,PARAMETER_MUST_NOT_BE_NULL.getMessage("itemID"));
        this.defaultValue = defaultValue;
        this.currentValue = defaultValue;
    }

    @Override
    public @NotNull I getKey() {
        return itemID;
    }

    @Override
    public void setCurrentValue(A value) {
        currentValue = value;
    }

    @Override
    public @Nullable A getCurrentValue() {
        return currentValue;
    }

    @Override
    public @Nullable A getDefaultValue() {
        return defaultValue;
    }
}
