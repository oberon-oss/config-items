package eu.oberon.oss.tools.configitems;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a default implementation of a storable configuration item that is part of a configuration system. This class provides mechanisms to manage
 * configuration data, conversion between internal and external keys, as well as application and storage type transformations.
 *
 * @param <K> The external key type used to identify the configuration item.
 * @param <A> The application data type representing the value of the configuration item.
 * @param <P> The storage type used for persisting the configuration item's value.
 * @param <I> The internal key type used within the configuration infrastructure.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
@Getter
public class DefaultStorableConfigurationItem<I, K, A, P> implements StorableConfigurationItem<I, K, A, P> {
    private final Class<I> intKeyType;
    private final Class<K> extKeyType;
    private final Class<A> applicationDataType;
    private final Class<P> storageType;

    private final Function<I, K> toExtKey;
    private final Function<K, I> toIntKey;
    private final Function<P, A> toDataType;
    private final Function<A, P> toStorageType;

    private final I itemID;
    private final A defaultValue;

    private final ConfigurationItemData<I, A> configurationItemData;

    /**
     * Constructs a new instance of DefaultStorableConfigurationItem using the provided builder.
     *
     * @param builder The builder containing the necessary parameters to construct the configuration item.
     *
     * @throws NullPointerException If the provided builder is null.
     * @since 1.0.0
     */

    DefaultStorableConfigurationItem(StorableConfigurationItemBuilderAccessor<I, K, A, P> builder) {
        Objects.requireNonNull(builder, "Parameter: builder");
        this.intKeyType = builder.getIntKeyType();
        this.extKeyType = builder.getExtKeyType();
        this.applicationDataType = builder.getApplicationDataType();
        this.storageType = builder.getStorageType();
        this.toExtKey = builder.getToExtKey();
        this.toIntKey = builder.getToIntKey();
        this.toDataType = builder.getToDataType();
        this.toStorageType = builder.getToStorageType();
        this.itemID = builder.getItemID();
        this.defaultValue = builder.getDefaultValue();

        configurationItemData = new StdConfigurationItemData<>(itemID, defaultValue);
    }

    /**
     * A standard implementation of {@link ConfigurationItemData}.
     *
     * @param <A>
     *
     * @author TigerLilly64
     * @since 1.0.0
     */
    private static class StdConfigurationItemData<I, A> implements ConfigurationItemData<I, A> {

        private final A defaultValue;
        private final I itemID;

        @Override
        public I getKey() {
            return itemID;
        }

        public StdConfigurationItemData(I itemID, A defaultValue) {
            this.defaultValue = defaultValue;
            this.itemID = itemID;
        }

        @Override
        public @Nullable A getDefaultValue() {
            return defaultValue;
        }

        private A currentValue;

        @Override
        public void setCurrentValue(A value) {
            currentValue = value;
        }

        @Override
        public @Nullable A getCurrentValue() {
            return currentValue;
        }

    }
}
