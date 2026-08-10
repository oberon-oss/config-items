package eu.oberon.oss.tools.configitems.builders;

import eu.oberon.oss.tools.configitems.converters.Converter;
import eu.oberon.oss.tools.configitems.converters.ConvertersRegistry;
import eu.oberon.oss.tools.configitems.items.ConfigurationItem;
import eu.oberon.oss.tools.configitems.items.ConfigurationItemAccessor;
import eu.oberon.oss.tools.configitems.items.DefaultStorableConfigurationItem;
import eu.oberon.oss.tools.configitems.items.StorableConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.StorageProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;

/**
 * A builder class for constructing immutable instances of {@link StorableConfigurationItem} instances with customizable types and conversion logic. This class
 * provides methods to set internal key, external key, application data, and storage types as well as mapping functions and default values.
 *
 * @param <K> The external key type used to reference the configuration item.
 * @param <A> The application data type representing the logical value of the configuration item.
 * @param <P> The physical storage type used to persist the configuration item.
 * @param <I> The internal key type used internally for referencing the item.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
class DefaultStorableConfigurationItemBuilder<I, K, A, P> implements StorableConfigurationItemBuilder<I, K, A, P> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStorableConfigurationItemBuilder.class);
    private static final ConvertersRegistry CONVERTERS_REGISTRY = new ConvertersRegistry();


    private Class<I> intKeyType;
    private Class<K> extKeyType;
    private Class<A> applicationDataType;
    private Class<P> storageType;

    private Function<I, K> toExtKey;
    private Function<P, A> toDataType;
    private Function<A, P> toStorageType;

    private I itemID;
    private A defaultValue;

    private StorageProvider storageProvider;

    private boolean autoGeneration = true;

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    DefaultStorableConfigurationItemBuilder() {
        // keep Javadoc happy
    }

    @Override
    public StorableConfigurationItemBuilder<I, K, A, P> setAutoGeneration(boolean autoGeneration) {
        this.autoGeneration = autoGeneration;
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>NOTE</b>:
     * <p>
     * if the internal and/or external key type is not {@code null}, then the type of internal and/or external key type will be set to the class of the item
     * ID.
     */
    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setItemID(@NotNull I itemID) {
        this.itemID = itemID;
        if (intKeyType == null) {
            //noinspection unchecked
            intKeyType = (Class<I>) itemID.getClass();
            LOGGER.info("Internal key type set to class of item ID: {}", intKeyType);
        }
        return this;
    }


    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setExtKeyType(@NotNull Class<K> extKeyType) {
        this.extKeyType = extKeyType;
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setApplicationDataType(@NotNull Class<A> applicationDataType) {
        this.applicationDataType = applicationDataType;
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>NOTE</b>:
     * <p>
     * if the value specified for the 'defaultValue' is not {@code null}, the 'applicationDataType' (if {@code null}) will be set to the class of the
     * 'defaultValue'.
     */
    @SuppressWarnings("unchecked")
    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setDefaultValue(@Nullable A defaultValue) {
        this.defaultValue = defaultValue;
        if (this.defaultValue != null && applicationDataType == null) {
            applicationDataType = (Class<A>) defaultValue.getClass();
            if (storageType == null)
                storageType = (Class<P>) defaultValue.getClass();
        }
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setStorageType(@NotNull Class<P> storageType) {
        this.storageType = storageType;
        return this;
    }

    @Override
    public StorableConfigurationItemBuilder<I, K, A, P> setStorageProvider(@NotNull StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setToExtKey(@NotNull Function<I, K> toExtKey) {
        this.toExtKey = toExtKey;
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setToDataType(@NotNull Function<P, A> toDataType) {
        this.toDataType = toDataType;
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setToStorageType(@NotNull Function<A, P> toStorageType) {
        this.toStorageType = toStorageType;
        return this;
    }


    @Override
    public StorableConfigurationItem<I, K, A, P> build() {
        Objects.requireNonNull(itemID, "ItemID type must not be null");
        Objects.requireNonNull(intKeyType, "Internal Key data type must not be null");
        Objects.requireNonNull(extKeyType, "External Key data type must not be null");
        Objects.requireNonNull(applicationDataType, "Application data type must not be null");
        Objects.requireNonNull(storageType, "Storage data type must not be null");
        Objects.requireNonNull(storageProvider, "Storage provider must not be null");

        if (!intKeyType.isInstance(itemID)) {
            throw new IllegalArgumentException("Item ID " + itemID + " is not an instance of internal key type " + intKeyType);
        }

        if (defaultValue != null && !applicationDataType.isInstance(defaultValue)) {
            throw new IllegalArgumentException("Default value " + defaultValue + " is not an instance of application data type " + applicationDataType);
        }

        if (!storageProvider.isStorageClassTypeAllowed(storageType)) {
            throw new IllegalArgumentException("Storage type " + storageType + " is not supported");
        }

        if (!storageProvider.isKeyClassTypeAllowed(extKeyType)) {
            throw new IllegalArgumentException("External key type " + extKeyType + " is not supported");
        }

        setDataConverters();
        setKeyConverters();

        if (defaultValue != null) {
            P convertedDefaultValue = toStorageType.apply(defaultValue);
            if (convertedDefaultValue != null && !storageType.isInstance(convertedDefaultValue)) {
                throw new IllegalArgumentException("Default value " + defaultValue + " is not an instance of storage data type " + storageType);
            }
        }

        ConfigurationItem<I, A> configurationItem = new DefaultConfigurationItem<>(itemID, defaultValue);

        ConfigurationItemAccessor<I, K, A, P> configurationItemAccessor = new DefaultConfigurationItemAccessor<>(
                intKeyType,
                extKeyType,
                applicationDataType,
                storageType,
                toExtKey,
                toDataType,
                toStorageType
        );

        return new DefaultStorableConfigurationItem<>(
                configurationItem,
                configurationItemAccessor,
                storageProvider
        );
    }


    @SuppressWarnings({"unchecked"})
    private <S, T> Function<S, T> getAutoConverter(Class<S> sourceType, Class<T> targetType) {
        if (sourceType.equals(targetType)) {
            return value -> (T) value;
        }

        if (targetType.equals(String.class)) {
            Converter<S> converter = CONVERTERS_REGISTRY.getConverterForClassType(sourceType);
            if (converter != null) {
                return value -> (T) converter.convertToString().apply(value);
            }
        }

        if (sourceType.equals(String.class)) {
            Converter<T> converter = CONVERTERS_REGISTRY.getConverterForClassType(targetType);
            if (converter != null) {
                return value -> converter.convertFromString().apply((String) value);
            }
        }

        return null;
    }

    private void setKeyConverters() {
        if (autoGeneration && toExtKey == null) {
            toExtKey = getAutoConverter(intKeyType, extKeyType);
        }

        Objects.requireNonNull(toExtKey, "'toExtKey()' function must not be null");
    }

    private void setDataConverters() {
        if (autoGeneration) {
            if (toDataType == null) {
                toDataType = getAutoConverter(storageType, applicationDataType);
            }
            if (toStorageType == null) {
                toStorageType = getAutoConverter(applicationDataType, storageType);
            }
        }

        Objects.requireNonNull(toDataType, "'toDataType()' function must not be null");
        Objects.requireNonNull(toStorageType, "'toStorageType()' function must not be null");
    }
}
