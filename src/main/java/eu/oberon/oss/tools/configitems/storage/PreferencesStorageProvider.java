package eu.oberon.oss.tools.configitems.storage;

import eu.oberon.oss.tools.configitems.items.ConfigurationItemAccessor;
import eu.oberon.oss.tools.configitems.items.StorableConfigurationItem;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.prefs.Preferences;

/**
 * Implementation of the {@link StorageProvider} interface that uses the preferences API to manage the storage and retrieval of configuration items. This
 * provider supports storing common data types and uses preference keys for data identification.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class PreferencesStorageProvider implements StorageProvider {

    private static final Set<Class<?>> SUPPORTED_DATA_STORAGE_TYPES = Set.of(
            String.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, byte[].class
    );

    private final Preferences preferences;

    /**
     * Constructs a {@code PreferencesStorageProvider} with the specified {@link Preferences} instance.
     *
     * @param preferences The {@link Preferences} instance used for storing and retrieving configuration data. Must not be null.
     *
     * @since 1.0.0
     */
    public PreferencesStorageProvider(@NotNull Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public <I, K, A, P> void storeConfigurationItem(StorableConfigurationItem<I, K, A, P> item) {
        storeTypedConfigurationItem(item);
    }

    @Override
    public <I, K, A, P> void loadConfigurationItem(StorableConfigurationItem<I, K, A, P> item) {
        loadTypedConfigurationItem(item);
    }

    @Override
    public boolean isKeyClassTypeAllowed(Class<?> keyClass) {
        return (keyClass.equals(String.class));
    }

    @Override
    public boolean isStorageClassTypeAllowed(Class<?> valueClass) {
        return SUPPORTED_DATA_STORAGE_TYPES.contains(valueClass);
    }

    private <I, K, A, P> void storeTypedConfigurationItem(StorableConfigurationItem<I, K, A, P> storableItem) {

        ConfigurationItemAccessor<I, K, A, P> accessor = storableItem.configurationItemAccessor();

        String key = String.valueOf(accessor.toExtKey().apply(storableItem.getKey()));
        P value = accessor.toStorageType().apply(storableItem.getCurrentValue());

        performStore(key, value);
    }

    private <I, K, A, P> void loadTypedConfigurationItem(StorableConfigurationItem<I, K, A, P> storableItem) {
        ConfigurationItemAccessor<I, K, A, P> accessor = storableItem.configurationItemAccessor();

        String key = String.valueOf(accessor.toExtKey().apply(storableItem.getKey()));
        P defaultValue = accessor.toStorageType().apply(storableItem.getDefaultValue());
        P storedValue = performLoad(key, defaultValue);

        storableItem.setCurrentValue(accessor.toDataType().apply(storedValue));
    }

    @SuppressWarnings("unchecked")
    private <P> P performLoad(String key, P defaultValue) {
        if (defaultValue == null) {
            return null;
        }

        return switch (defaultValue) {
            case String string -> (P) preferences.get(key, string);
            case Integer integer -> (P) Integer.valueOf(preferences.getInt(key, integer));
            case Boolean booleanValue -> (P) Boolean.valueOf(preferences.getBoolean(key, booleanValue));
            case Long longValue -> (P) Long.valueOf(preferences.getLong(key, longValue));
            case Float floatValue -> (P) Float.valueOf(preferences.getFloat(key, floatValue));
            case Double doubleValue -> (P) Double.valueOf(preferences.getDouble(key, doubleValue));
            case byte[] bytes -> (P) preferences.getByteArray(key, bytes);
            default -> throw new IllegalArgumentException("Unsupported type: " + defaultValue.getClass().getName());
        };
    }

    private <P> void performStore(String key, P value) {
        if (value == null) {
            return;
        }

        switch (value) {
            case String string -> preferences.put(key, string);
            case Integer integer -> preferences.putInt(key, integer);
            case Boolean booleanValue -> preferences.putBoolean(key, booleanValue);
            case Long longValue -> preferences.putLong(key, longValue);
            case Float floatValue -> preferences.putFloat(key, floatValue);
            case Double doubleValue -> preferences.putDouble(key, doubleValue);
            case byte[] bytes -> preferences.putByteArray(key, bytes);
            default -> throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }
    }
}