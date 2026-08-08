package eu.oberon.oss.tools.configitems;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.prefs.Preferences;

/**
 * A builder class for constructing immutable instances of {@link DefaultStorableConfigurationItem} with customizable types and conversion logic. This class
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
public class DefaultStorableConfigurationItemBuilder<I, K, A, P> implements StorableConfigurationItemBuilder<I, K, A, P> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStorableConfigurationItemBuilder.class);

    /**
     * The set of supported types for the {@link Preferences} class and the DefaultConfigurationItem class.
     */
    public static final Set<Class<?>> SUPPORTED_DATA_STORAGE_TYPES = Set.of(
            String.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, byte[].class
    );

    private Class<I> intKeyType;
    private Class<K> extKeyType;
    private Class<A> applicationDataType;
    private Class<P> storageType;

    private Function<I, K> toExtKey;
    private Function<K, I> toIntKey;
    private Function<P, A> toDataType;
    private Function<A, P> toStorageType;

    private I itemID;
    private A defaultValue;

    private final boolean autoGeneration;

    public DefaultStorableConfigurationItemBuilder() {
        this(true);
    }

    public DefaultStorableConfigurationItemBuilder(boolean autoGeneration) {
        this.autoGeneration = autoGeneration;
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
        if (extKeyType == null) {
            //noinspection unchecked
            extKeyType = (Class<K>) itemID.getClass();
            LOGGER.info("External key type set to class of item ID: {}", extKeyType);
        }
        return this;
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>NOTE</b>:
     * <p>
     * if the internal key type is not {@code null}, the internal key type will be set to the class of the external key type.
     */
    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setExtKeyType(@NotNull Class<K> extKeyType) {
        this.extKeyType = extKeyType;
        if (intKeyType == null) {
            //noinspection unchecked
            intKeyType = (Class<I>) extKeyType;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>NOTE</b>:
     * <p>
     * if the value specified for the 'applicationDataType' is not null, the 'storageType' will be set to the class of the 'applicationDataType'.
     */
    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setApplicationDataType(@NotNull Class<A> applicationDataType) {
        this.applicationDataType = applicationDataType;
        if (storageType == null) {
            //noinspection unchecked
            storageType = (Class<P>) applicationDataType;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>NOTE</b>:
     * <p>
     * if the value specified for the 'defaultValue' is not {@code null},
     * <ul>
     *     <li>the 'applicationDataType' will be set to the class of the 'defaultValue'.</li>
     *     <li>the 'storageType' will be set to the class of the 'defaultValue'.</li>
     * </ul>
     */
    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setDefaultValue(@Nullable A defaultValue) {
        this.defaultValue = defaultValue;
        if (this.defaultValue != null) {
            //noinspection unchecked
            applicationDataType = (Class<A>) defaultValue.getClass();
        }
        if (storageType == null) {
            //noinspection unchecked
            storageType = (Class<P>) applicationDataType;
        }
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setStorageType(@NotNull Class<P> storageType) {
        if (!SUPPORTED_DATA_STORAGE_TYPES.contains(storageType)) {
            throw new IllegalArgumentException("Storage type " + storageType + " is not supported");
        }
        this.storageType = storageType;
        //noinspection unchecked
        applicationDataType = (Class<A>) defaultValue.getClass();
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setToExtKey(@NotNull Function<I, K> toExtKey) {
        this.toExtKey = toExtKey;
        return this;
    }

    @Override
    public DefaultStorableConfigurationItemBuilder<I, K, A, P> setToIntKey(@NotNull Function<K, I> toIntKey) {
        this.toIntKey = toIntKey;
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

    /**
     * {@inheritDoc}
     * <p>
     * <b><i>Additional Notes</i></b>:
     * If autegeneration was enabled at construction time, automatic conversion functions for conversion between
     * <ul>
     *     <li> - Internal and external keys are created if</li>
     *     <ol>
     *         <li>the internal key type and external key type are the same</li>
     *         <li>the internal key type is a string, and the external key type is an enum</li>
     *         <li>the internal key type is an enum, and the external key type is a string</li>
     *     </ol>
     *     <li> - Application data type and storage type are created if</li>
     *     <ol>
     *         <li>the application data type and storage type are the same</li>
     *         <li>the application data type is a string, and the storage type is an enum</li>
     *         <li>the application data type is an enum, and the storage type is a string</li>
     *     </ol>
     * </ul>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public StorableConfigurationItem<I, K, A, P> build() {
        Objects.requireNonNull(itemID, "ItemID type must not be null");
        Objects.requireNonNull(intKeyType, "Internal Key data type must not be null");
        Objects.requireNonNull(extKeyType, "External Key data type must not be null");
        Objects.requireNonNull(applicationDataType, "Application data type must not be null");
        Objects.requireNonNull(storageType, "Storage data type must not be null");


        if (autoGeneration) {
            // If the internal key type and external key type are the same, we can provide a default implementation.
            if (extKeyType.equals(intKeyType)) {
                toIntKey = k -> (I) k;
                toExtKey = i -> (K) i;
            } else if (extKeyType.isEnum() && intKeyType.equals(String.class)) {
                toIntKey = k -> (I) Enum.valueOf((Class<Enum>) extKeyType, (String) k);
                toExtKey = i -> (K) Enum.valueOf((Class<Enum>) extKeyType, (String) i);
            } else if (extKeyType.equals(String.class) && intKeyType.isEnum()) {
                toIntKey = k -> (I) Enum.valueOf((Class<Enum>) intKeyType, (String) k);
                toExtKey = i -> (K) Enum.valueOf((Class<Enum>) intKeyType, (String) i);
            }
        }

        Objects.requireNonNull(toIntKey, "'toIntKey()' function must not be null");
        Objects.requireNonNull(toExtKey, "'toExtKey()' function must not be null");

        if (autoGeneration) {
            if (applicationDataType.equals(storageType)) {
                toDataType = value -> (A) (value);
                toStorageType = value -> (P) value;
            } else if (applicationDataType.equals(String.class) && storageType.isEnum()) {
                toDataType = value -> (A) Enum.valueOf((Class<Enum>) applicationDataType, (String) value);
                toStorageType = value -> (P) ((Enum<?>) value).name();
            } else if (storageType.equals(String.class) && applicationDataType.isEnum()) {
                toDataType = value -> (A) Enum.valueOf((Class<Enum>) applicationDataType, (String) value);
                toStorageType = value -> (P) ((Enum<?>) value).name();
            }
        }

        Objects.requireNonNull(toDataType, "'toDataType()' function must not be null");
        Objects.requireNonNull(toStorageType, "'toStorageType()' function must not be null");

        return new DefaultStorableConfigurationItem<>(new StorableConfigurationItemBuilderAccessor<>() {
            @Override
            public Class<I> getIntKeyType() {
                return intKeyType;
            }

            @Override
            public Class<K> getExtKeyType() {
                return extKeyType;
            }

            @Override
            public Class<A> getApplicationDataType() {
                return applicationDataType;
            }

            @Override
            public Class<P> getStorageType() {
                return storageType;
            }

            @Override
            public Function<I, K> getToExtKey() {
                return toExtKey;
            }

            @Override
            public Function<K, I> getToIntKey() {
                return toIntKey;
            }

            @Override
            public Function<P, A> getToDataType() {
                return toDataType;
            }

            @Override
            public Function<A, P> getToStorageType() {
                return toStorageType;
            }

            @Override
            public I getItemID() {
                return itemID;
            }

            @Override
            public A getDefaultValue() {
                return defaultValue;
            }
        });
    }
}
