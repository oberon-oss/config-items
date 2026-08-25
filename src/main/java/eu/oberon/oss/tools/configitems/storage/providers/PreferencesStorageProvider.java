package eu.oberon.oss.tools.configitems.storage.providers;

import eu.oberon.oss.tools.configitems.storage.StorableConfigurationItem;

import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static eu.oberon.oss.tools.configitems.ConfigItems.TYPE_NOT_SUPPORTED;

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
    public PreferencesStorageProvider(Preferences preferences) {
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

    @Override
    public void flush() throws BackingStoreException {
        preferences.flush();
    }

    private <I, K, A, P> void storeTypedConfigurationItem(StorableConfigurationItem<I, K, A, P> storableItem) {
        String key = String.valueOf(storableItem.toExtKey().apply(storableItem.getKey()));
        P value = storableItem.toStorageType().apply(storableItem.getCurrentValue());

        performStore(key, value);
    }

    private <I, K, A, P> void loadTypedConfigurationItem(StorableConfigurationItem<I, K, A, P> storableItem) {
        String key = String.valueOf(storableItem.toExtKey().apply(storableItem.getKey()));
        P defaultValue = storableItem.toStorageType().apply(storableItem.getDefaultValue());
        P storedValue = performLoad(key, storableItem.storageType(), defaultValue);

        storableItem.setCurrentValue(storableItem.toDataType().apply(storedValue));
    }

    private boolean containsKey(String key) {
        return preferences.get(key, null) != null;
    }

    @SuppressWarnings("unchecked")
    private <P> P performLoad(String key, Class<P> storageType, P defaultValue) {
        if (!containsKey(key)) {
            return defaultValue;
        }

        return switch (storageType.getName()) {
            case "java.lang.String" -> (P) preferences.get(key, (String) defaultValue);
            case "java.lang.Integer" -> (P) Integer.valueOf(preferences.getInt(key, defaultValue == null ? 0 : (Integer) defaultValue));
            case "java.lang.Boolean" -> (P) Boolean.valueOf(preferences.getBoolean(key, defaultValue != null && (Boolean) defaultValue));
            case "java.lang.Long" -> (P) Long.valueOf(preferences.getLong(key, defaultValue == null ? 0L : (Long) defaultValue));
            case "java.lang.Float" -> (P) Float.valueOf(preferences.getFloat(key, defaultValue == null ? 0.0F : (Float) defaultValue));
            case "java.lang.Double" -> (P) Double.valueOf(preferences.getDouble(key, defaultValue == null ? 0.0D : (Double) defaultValue));
            case "[B" -> (P) preferences.getByteArray(key, (byte[]) defaultValue);
            default -> throw TYPE_NOT_SUPPORTED.getException(IllegalArgumentException.class, storageType.getName());
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
            default -> throw TYPE_NOT_SUPPORTED.getException(IllegalArgumentException.class, value.getClass().getName());
        }
    }
}