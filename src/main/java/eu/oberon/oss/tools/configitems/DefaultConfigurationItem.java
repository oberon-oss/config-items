package eu.oberon.oss.tools.configitems;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.prefs.Preferences;

/**
 * Implementation that allows for manipulating configuration items.
 * <p>
 * The items are stored to/retrieved from {@link Preferences}. This implementation supports conversion from non-preference types to types supported by the
 * application and vice versa.
 *
 * @param <A> The data type of the configuration item as used within an application.
 * @param <P> The data type in which data is persisted within the {@link Preferences} class.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public final class DefaultConfigurationItem<A, P> implements ConfigurationItem<A> {

    /**
     * The set of supported types for the {@link Preferences} class and the DefaultConfigurationItem class.
     */
    public static final Set<Class<?>> SUPPORTED_PREFERENCES_TYPES = Set.of(
            String.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, byte[].class
    );

    private final String itemName;
    private final Class<P> storageType;
    private final Function<P, A> toDataType;
    private final Function<A, P> toStorageType;
    private final A defaultValue;

    private A configItemValue;

    @SuppressWarnings("unused")
    private DefaultConfigurationItem() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Creates a new configuration item where the application data type and the {@link Preferences} storage type are the same.
     *
     * @param configItemName The name of the configuration item.
     * @param storageType    The storage type of the configuration item. This type must be one of the supported types by the {@link Preferences} class.
     *
     * @throws IllegalStateException if the storage type is not supported by the {@link Preferences} class.
     * @since 1.0.0
     */
    private DefaultConfigurationItem(String configItemName, Class<P> storageType) {
        this(configItemName, storageType, null);
    }

    /**
     * Creates a new configuration item where the application data type and the {@link Preferences} storage type are the same.
     *
     * @param configItemName The name of the configuration item.
     * @param storageType    The storage type of the configuration item. This type must be one of the supported types by the {@link Preferences} class.
     * @param defaultValue   The default value of the configuration item. A value of {@code null} indicates that no default value is set.
     *
     * @throws IllegalStateException if the storage type is not supported by the {@link Preferences} class.
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    private DefaultConfigurationItem(String configItemName, Class<P> storageType, A defaultValue) {
        this(
                configItemName,
                storageType,
                defaultValue,
                value -> (P) value,
                value -> (A) value
        );
    }

    /**
     * Creates an instance of DefaultConfigurationItem that supports data types that are not directly supported by the {@link Preferences} class.
     * <p>
     * The provided conversion methods in {@code toDataType} and {@code toStorageType} are used to convert between the data as it is used in the application,
     * and the type of data it is stored in within the {@link Preferences} class.
     *
     * @param configItemName The name of the configuration item.
     * @param storageType    The storage type of the configuration item. This type must be one of the supported types by the {@link Preferences} class.
     * @param defaultValue   The default value of the configuration item. A value of {@code null} indicates that no default value is set.
     * @param toStorageType  The function to convert the application data type to the storage type.
     * @param toDataType     The function to convert the storage type to the application data type.
     *
     * @throws IllegalStateException if the storage type is not supported by the {@link Preferences} class.
     * @since 1.0.0
     */
    private DefaultConfigurationItem(
            String configItemName,
            Class<P> storageType,
            A defaultValue,
            Function<A, P> toStorageType,
            Function<P, A> toDataType
    ) {
        this.itemName = Objects.requireNonNull(configItemName, "configItemName");
        this.storageType = Objects.requireNonNull(storageType, "storageType");
        this.defaultValue = defaultValue;
        this.toStorageType = Objects.requireNonNull(toStorageType, "toStorageType");
        this.toDataType = Objects.requireNonNull(toDataType, "toDataType");

        if (!SUPPORTED_PREFERENCES_TYPES.contains(storageType)) {
            throw new IllegalStateException("Not an allowed type for a Preferences instance: " + storageType);
        }
    }

    /**
     * Creates a configuration item where the application data type and the {@link Preferences} storage type are the same.
     *
     * @param configItemName The name of the configuration item.
     * @param storageType    The storage type of the configuration item.
     * @param <T>            The application and storage type.
     *
     * @return The configuration item.
     *
     * @since 1.0.0
     */
    public static <T> ConfigurationItem<T> getInstance(String configItemName, Class<T> storageType) {
        return new DefaultConfigurationItem<>(configItemName, storageType);
    }

    /**
     * Creates a configuration item where the application data type and the {@link Preferences} storage type are the same.
     *
     * @param configItemName The name of the configuration item.
     * @param storageType    The storage type of the configuration item.
     * @param defaultValue   The default value.
     * @param <T>            The application and storage type.
     *
     * @return The configuration item.
     *
     * @since 1.0.0
     */
    public static <T> ConfigurationItem<T> getInstance(String configItemName, Class<T> storageType, T defaultValue) {
        return new DefaultConfigurationItem<>(configItemName, storageType, defaultValue);
    }

    /**
     * Creates a configuration item where the application data type and the {@link Preferences} storage type are the same. The storage type is derived from the
     * provided default value.
     * <p>
     * If the supplied default value is an {@link Enum}, it is stored as the enum constant name and loaded with {@link Enum#valueOf(Class, String)}.
     *
     * @param configItemName The name of the configuration item.
     * @param defaultValue   The default value.
     * @param <T>            The application and storage type, unless the value is an enum.
     *
     * @return The configuration item.
     *
     * @throws NullPointerException if {@code defaultValue} is {@code null}.
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <T> ConfigurationItem<T> getInstance(String configItemName, T defaultValue) {
        Objects.requireNonNull(defaultValue, "defaultValue");

        if (defaultValue instanceof Enum<?>) {
            return getEnumInstanceFromDefaultValue(configItemName, defaultValue);
        }

        return new DefaultConfigurationItem<>(configItemName, (Class<T>) defaultValue.getClass(), defaultValue);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> ConfigurationItem<T> getEnumInstanceFromDefaultValue(String configItemName, T defaultValue) {
        Enum enumValue = (Enum) defaultValue;
        Class enumType = enumValue.getDeclaringClass();

        return getEnumInstance(configItemName, enumType, enumValue);
    }

    /**
     * Creates a configuration item where the application data type differs from the {@link Preferences} storage type.
     *
     * @param configItemName The name of the configuration item.
     * @param storageType    The storage type of the configuration item.
     * @param defaultValue   The default application value.
     * @param toStorageType  The function to convert the application data type to the storage type.
     * @param toDataType     The function to convert the storage type to the application data type.
     * @param <A>            The application data type.
     * @param <P>            The preferences' storage type.
     *
     * @return The configuration item.
     *
     * @since 1.0.0
     */
    public static <A, P> ConfigurationItem<A> getConvertedInstance(
            String configItemName,
            Class<P> storageType,
            A defaultValue,
            Function<A, P> toStorageType,
            Function<P, A> toDataType
    ) {
        return new DefaultConfigurationItem<>(configItemName, storageType, defaultValue, toStorageType, toDataType);
    }

    /**
     * Creates a configuration item where the value is stored in {@link Preferences} as a {@link String}.
     * <p>
     * The application value is converted to the stored value using {@link Object#toString()}. Loading still requires an explicit {@code fromString} converter,
     * because there is no generally safe way to convert a {@link String} back to an arbitrary application type.
     *
     * @param configItemName The name of the configuration item.
     * @param defaultValue   The default application value.
     * @param fromString     The function to convert the stored string value back to the application data type.
     * @param <A>            The application data type.
     *
     * @return The configuration item.
     *
     * @since 1.0.0
     */
    public static <A> ConfigurationItem<A> getStringBackedInstance(
            String configItemName,
            A defaultValue,
            Function<String, A> fromString
    ) {
        return new DefaultConfigurationItem<>(
                configItemName,
                String.class,
                defaultValue,
                Object::toString,
                fromString
        );
    }

    /**
     * Creates a configuration item where the application data type is an enum and the value is stored in {@link Preferences} as a {@link String}.
     * <p>
     * The stored string value is the enum constant's {@link Enum#name()}. The value is loaded using {@link Enum#valueOf(Class, String)}.
     *
     * @param configItemName The name of the configuration item.
     * @param enumType       The enum type.
     * @param defaultValue   The default enum value. A value of {@code null} indicates that no default value is set.
     * @param <E>            The enum type.
     *
     * @return The configuration item.
     *
     * @since 1.0.0
     */
    public static <E extends Enum<E>> ConfigurationItem<E> getEnumInstance(
            String configItemName,
            Class<E> enumType,
            E defaultValue
    ) {
        Objects.requireNonNull(enumType, "enumType");

        return new DefaultConfigurationItem<>(
                configItemName,
                String.class,
                defaultValue,
                Enum::name,
                value -> Enum.valueOf(enumType, value)
        );
    }

    /**
     * Creates a configuration item where the application data type is an enum and the value is stored in {@link Preferences} as a {@link String}.
     * <p>
     * This overload is useful when there is no default value.
     *
     * @param configItemName The name of the configuration item.
     * @param enumType       The enum type.
     * @param <E>            The enum type.
     *
     * @return The configuration item.
     *
     * @since 1.0.0
     */
    public static <E extends Enum<E>> ConfigurationItem<E> getEnumInstance(
            String configItemName,
            Class<E> enumType
    ) {
        return getEnumInstance(configItemName, enumType, null);
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    @Override
    public void setConfigItemValue(A value) {
        configItemValue = value;
    }

    @Override
    public A getConfigItemValue() {
        return configItemValue;
    }

    @Override
    public void store(Preferences preferences) {
        Objects.requireNonNull(preferences, "preferences");

        if (configItemValue == null) {
            return;
        }

        P preferenceValue = Objects.requireNonNull(
                toStorageType.apply(configItemValue),
                "Converted preference value cannot be null"
        );

        storePreferenceValue(preferences, preferenceValue);
    }

    @Override
    public void load(Preferences preferences) {
        Objects.requireNonNull(preferences, "preferences");

        if (!preferenceExists(preferences)) {
            configItemValue = defaultValue;
            return;
        }

        P preferenceValue = loadPreferenceValue(preferences);
        configItemValue = preferenceValue == null ? null : toDataType.apply(preferenceValue);
    }

    private boolean preferenceExists(Preferences preferences) {
        return preferences.get(itemName, null) != null;
    }

    private void storePreferenceValue(Preferences preferences, P preferenceValue) {
        if (storageType == String.class) {
            preferences.put(itemName, (String) preferenceValue);
            return;
        }

        if (storageType == Integer.class) {
            preferences.putInt(itemName, (Integer) preferenceValue);
            return;
        }

        if (storageType == Long.class) {
            preferences.putLong(itemName, (Long) preferenceValue);
            return;
        }

        if (storageType == Boolean.class) {
            preferences.putBoolean(itemName, (Boolean) preferenceValue);
            return;
        }

        if (storageType == Float.class) {
            preferences.putFloat(itemName, (Float) preferenceValue);
            return;
        }

        if (storageType == Double.class) {
            preferences.putDouble(itemName, (Double) preferenceValue);
            return;
        }

        if (storageType == byte[].class) {
            preferences.putByteArray(itemName, (byte[]) preferenceValue);
            return;
        }

        throw new IllegalStateException("Unexpected/Unsupported data type: " + storageType);
    }

    @SuppressWarnings({"unchecked","java:S3776"})
    private P loadPreferenceValue(Preferences preferences) {
        P storageDefaultValue = defaultValue == null ? null : toStorageType.apply(defaultValue);

        if (storageType == String.class) {
            return (P) preferences.get(itemName, (String) storageDefaultValue);
        }

        if (storageType == Integer.class) {
            int fallbackValue = storageDefaultValue == null ? 0 : (Integer) storageDefaultValue;
            return (P) Integer.valueOf(preferences.getInt(itemName, fallbackValue));
        }

        if (storageType == Long.class) {
            long fallbackValue = storageDefaultValue == null ? 0L : (Long) storageDefaultValue;
            return (P) Long.valueOf(preferences.getLong(itemName, fallbackValue));
        }

        if (storageType == Boolean.class) {
            boolean fallbackValue = storageDefaultValue != null && (Boolean) storageDefaultValue;
            return (P) Boolean.valueOf(preferences.getBoolean(itemName, fallbackValue));
        }

        if (storageType == Float.class) {
            float fallbackValue = storageDefaultValue == null ? 0.0F : (Float) storageDefaultValue;
            return (P) Float.valueOf(preferences.getFloat(itemName, fallbackValue));
        }

        if (storageType == Double.class) {
            double fallbackValue = storageDefaultValue == null ? 0.0D : (Double) storageDefaultValue;
            return (P) Double.valueOf(preferences.getDouble(itemName, fallbackValue));
        }

        if (storageType == byte[].class) {
            return (P) preferences.getByteArray(itemName, (byte[]) storageDefaultValue);
        }

        throw new IllegalStateException("Unexpected/Unsupported data type: " + storageType);
    }
}