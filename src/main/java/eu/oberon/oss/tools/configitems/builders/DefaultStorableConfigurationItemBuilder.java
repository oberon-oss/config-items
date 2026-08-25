package eu.oberon.oss.tools.configitems.builders;


import eu.oberon.oss.tools.configitems.items.ConfigurationItem;
import eu.oberon.oss.tools.configitems.items.DefaultConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.DefaultStorableConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.StorableConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.providers.StorageProvider;
import eu.oberon.oss.tools.converters.ConvertersRegistry;
import eu.oberon.oss.tools.converters.string.Converter;
import org.jetbrains.annotations.Nullable;
import org.slf4j.event.Level;

import java.util.Objects;
import java.util.function.Function;

import static eu.oberon.oss.tools.configitems.ConfigItems.*;

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
    private final ConvertersRegistry convertersRegistry;

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
        this(new ConvertersRegistry());
    }

    /**
     * Constructs a new instance of {@code DefaultStorableConfigurationItemBuilder} with the specified {@code convertersRegistry}.
     *
     * @param convertersRegistry the registry containing converters for key, data, and storage type transformations; must not be null.
     *
     * @throws NullPointerException if {@code convertersRegistry} is null.
     * @since 1.0.0
     */
    DefaultStorableConfigurationItemBuilder(ConvertersRegistry convertersRegistry) {
        this.convertersRegistry = Objects.requireNonNull(convertersRegistry, "convertersRegistry");
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
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setItemID(I itemID) {
        this.itemID = itemID;
        if (intKeyType == null) {
            //noinspection unchecked
            intKeyType = (Class<I>) itemID.getClass();
            INTERNAL_KEY_TYPE_SET.logMessage(Level.DEBUG, (Object[]) new Class[]{intKeyType});
        }
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setExtKeyType(Class<K> extKeyType) {
        this.extKeyType = extKeyType;
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setApplicationDataType(Class<A> applicationDataType) {
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
    @SuppressWarnings({"unchecked", "java:S3878"})
    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setDefaultValue(@Nullable A defaultValue) {
        this.defaultValue = defaultValue;
        if (this.defaultValue != null && applicationDataType == null) {
            applicationDataType = (Class<A>) defaultValue.getClass();
            APPLICATION_DATA_TYPE_SET.logMessage(Level.DEBUG, new Object[]{applicationDataType});
            if (storageType == null) {
                STORAGE_TYPE_SET_TO.logMessage(Level.DEBUG, new Object[]{applicationDataType});
                storageType = (Class<P>) defaultValue.getClass();
            }
        }
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setStorageType(Class<P> storageType) {
        this.storageType = storageType;
        return this;
    }

    @Override
    public StorableConfigurationItemBuilder<I, K, A, P> setStorageProvider(StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setToExtKey(Function<I, K> toExtKey) {
        this.toExtKey = toExtKey;
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setToDataType(Function<P, A> toDataType) {
        this.toDataType = toDataType;
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setToStorageType(Function<A, P> toStorageType) {
        this.toStorageType = toStorageType;
        return this;
    }


    @Override
    public StorableConfigurationItem<I, K, A, P> build() {
        Objects.requireNonNull(itemID, PARAMETER_MUST_NOT_BE_NULL.getMessage("itemID"));
        Objects.requireNonNull(intKeyType, PARAMETER_MUST_NOT_BE_NULL.getMessage("intKeyType"));
        Objects.requireNonNull(extKeyType, PARAMETER_MUST_NOT_BE_NULL.getMessage("extKeyType"));
        Objects.requireNonNull(applicationDataType, PARAMETER_MUST_NOT_BE_NULL.getMessage("applicationDataType"));
        Objects.requireNonNull(storageType, PARAMETER_MUST_NOT_BE_NULL.getMessage("storageType"));
        Objects.requireNonNull(storageProvider, PARAMETER_MUST_NOT_BE_NULL.getMessage("storageProvider"));

        if (!intKeyType.isInstance(itemID)) {
            throw TYPE_MUST_BE_INSTANCE_OF.getException(IllegalArgumentException.class, itemID, intKeyType);
        }

        if (defaultValue != null && !applicationDataType.isInstance(defaultValue)) {
            throw DEFAULT_VALUE_MUST_BE_INSTANCE_OF.getException(IllegalArgumentException.class, defaultValue, applicationDataType);
        }

        if (!storageProvider.isStorageClassTypeAllowed(storageType)) {
            throw STORAGE_TYPE_IS_NOT_SUPPORTED.getException(IllegalArgumentException.class, storageType);
        }

        if (!storageProvider.isKeyClassTypeAllowed(extKeyType)) {
            throw EXTERNAL_KEY_TYPE_NOT_SUPPORTED.getException(IllegalArgumentException.class, extKeyType);
        }

        setDataConverters();
        setKeyConverters();

        if (defaultValue != null) {
            P convertedDefaultValue = toStorageType.apply(defaultValue);
            if (convertedDefaultValue != null && !storageType.isInstance(convertedDefaultValue)) {
                throw DEFAULT_VALUE_TYPES_DO_NOT_MATCH.getException(IllegalArgumentException.class, defaultValue, storageType);
            }
        }

        ConfigurationItem<I, A, P> configurationItem = new DefaultConfigurationItem<>(itemID, defaultValue, applicationDataType, storageType, toDataType, toStorageType);

        return new DefaultStorableConfigurationItem<>(configurationItem, intKeyType, extKeyType, toExtKey, storageProvider);
    }


    @SuppressWarnings({"unchecked"})
    private <S, T> Function<S, T> getAutoConverter(Class<S> sourceType, Class<T> targetType) {
        if (sourceType.equals(targetType)) {
            return value -> (T) value;
        }

        if (targetType.equals(String.class)) {
            Converter<S> converter = convertersRegistry.getConverterForClassType(sourceType);
            if (converter != null) {
                return value -> (T) converter.convertToString().apply(value);
            }
        }

        if (sourceType.equals(String.class)) {
            Converter<T> converter = convertersRegistry.getConverterForClassType(targetType);
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
        Objects.requireNonNull(toExtKey, FUNCTION_MUST_NOT_BE_NULL.getMessage("toExtKey()"));
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

        Objects.requireNonNull(toDataType, FUNCTION_MUST_NOT_BE_NULL.getMessage("toDataType()"));
        Objects.requireNonNull(toStorageType, FUNCTION_MUST_NOT_BE_NULL.getMessage("toStorageType()"));
    }
}
