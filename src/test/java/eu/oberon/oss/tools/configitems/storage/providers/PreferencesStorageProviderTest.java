package eu.oberon.oss.tools.configitems.storage.providers;

import eu.oberon.oss.tools.configitems.storage.StorableConfigurationItem;

import eu.oberon.oss.tools.configitems.builders.StorableConfigurationItemBuilder;
import eu.oberon.oss.tools.configitems.builders.StorableConfigurationItemBuilderFactory;
import eu.oberon.oss.tools.configitems.items.ConfigurationItemAccessor;
import eu.oberon.oss.tools.converters.ConvertersRegistry;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;
import java.util.function.Function;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesStorageProviderTest {

    private Preferences preferences;
    private PreferencesStorageProvider storageProvider;
    private StorableConfigurationItemBuilderFactory builderFactory;
    private final ConvertersRegistry convertersRegistry = new ConvertersRegistry();

    static Stream<Arguments> supportedStorageTypes() {
        return Stream.of(Arguments.of(String.class), Arguments.of(Integer.class), Arguments.of(Long.class), Arguments.of(Float.class), Arguments.of(Double.class), Arguments.of(Boolean.class), Arguments.of(byte[].class));
    }

    static Stream<Arguments> unsupportedStorageTypes() {
        return Stream.of(Arguments.of(Byte.class), Arguments.of(Short.class), Arguments.of(Character.class), Arguments.of(Object.class), Arguments.of(UUID.class));
    }

    static Stream<Arguments> supportedRoundTripValues() {
        return Stream.of(Arguments.of("string-key", String.class, "default", "stored"), Arguments.of("integer-key", Integer.class, 123, 456), Arguments.of("long-key", Long.class, 123L, 456L), Arguments.of("float-key", Float.class, 123.5F, 456.5F), Arguments.of("double-key", Double.class, 123.5D, 456.5D), Arguments.of("boolean-key", Boolean.class, false, true), Arguments.of("bytes-key", byte[].class, new byte[]{1, 2, 3}, new byte[]{4, 5, 6}));
    }

    static Stream<Arguments> invalidStoredPrimitiveValues() {
        return Stream.of(Arguments.of("invalid-integer-key", Integer.class, 123), Arguments.of("invalid-long-key", Long.class, 123L), Arguments.of("invalid-float-key", Float.class, 123.5F), Arguments.of("invalid-double-key", Double.class, 123.5D), Arguments.of("invalid-boolean-key", Boolean.class, false));
    }

    @BeforeEach
    void setUp() throws BackingStoreException {
        preferences = Preferences.userRoot().node("/eu/oberon/oss/tools/configitems/test/" + UUID.randomUUID());
        preferences.clear();

        storageProvider = new PreferencesStorageProvider(preferences);
        builderFactory = StorableConfigurationItemBuilderFactory.create(storageProvider, convertersRegistry);
    }

    @AfterEach
    void tearDown() throws BackingStoreException {
        preferences.removeNode();
    }

    @ParameterizedTest
    @MethodSource("supportedStorageTypes")
    void isStorageClassTypeAllowedReturnsTrueForSupportedTypes(Class<?> storageType) {
        assertTrue(storageProvider.isStorageClassTypeAllowed(storageType));
    }

    @ParameterizedTest
    @MethodSource("unsupportedStorageTypes")
    void isStorageClassTypeAllowedReturnsFalseForUnsupportedTypes(Class<?> storageType) {
        assertFalse(storageProvider.isStorageClassTypeAllowed(storageType));
    }

    @Test
    void isKeyClassTypeAllowedOnlyAllowsStringKeys() {
        assertTrue(storageProvider.isKeyClassTypeAllowed(String.class));

        assertFalse(storageProvider.isKeyClassTypeAllowed(Integer.class));
        assertFalse(storageProvider.isKeyClassTypeAllowed(Long.class));
        assertFalse(storageProvider.isKeyClassTypeAllowed(Object.class));
    }

    @ParameterizedTest
    @MethodSource("supportedRoundTripValues")
    <A> void loadUsesDefaultValueWhenNoStoredValueExists(String key, Class<A> valueType, A defaultValue, A ignoredStoredValue) {
        StorableConfigurationItem<String, String, A, A> item = createSameTypeItem(key, valueType, defaultValue);

        item.setCurrentValue(null);
        item.load();

        assertValueEquals(defaultValue, item.getCurrentValue());
    }

    @ParameterizedTest
    @MethodSource("supportedRoundTripValues")
    <A> void saveAndLoadRoundTripsSupportedStorageTypes(String key, Class<A> valueType, A defaultValue, A storedValue) {
        StorableConfigurationItem<String, String, A, A> item = createSameTypeItem(key, valueType, defaultValue);

        item.setCurrentValue(storedValue);
        item.save();

        item.setCurrentValue(defaultValue);
        item.load();

        assertValueEquals(storedValue, item.getCurrentValue());
    }

    @ParameterizedTest
    @MethodSource("supportedRoundTripValues")
    <A> void loadUsesStoredValueInsteadOfDefaultValue(String key, Class<A> valueType, A defaultValue, A storedValue) {
        StorableConfigurationItem<String, String, A, A> item = createSameTypeItem(key, valueType, defaultValue);

        item.setCurrentValue(storedValue);
        item.save();

        StorableConfigurationItem<String, String, A, A> reloadedItem = createSameTypeItem(key, valueType, defaultValue);
        reloadedItem.load();

        assertValueEquals(storedValue, reloadedItem.getCurrentValue());
    }

    @Test
    void loadWithNullDefaultAndNoStoredValueSetsCurrentValueToNull() {
        StorableConfigurationItem<String, String, String, String> item = builderFactory.<String, String, String, String>getInstance().setItemID("null-default-missing-key").setApplicationDataType(String.class).setStorageType(String.class).build();

        item.setCurrentValue("temporary");
        item.load();

        assertNull(item.getCurrentValue());
    }

    @Test
    void loadWithNullDefaultUsesStoredStringWhenPresent() {
        preferences.put("null-default-existing-string-key", "stored-value");

        StorableConfigurationItem<String, String, String, String> item = builderFactory.<String, String, String, String>getInstance().setItemID("null-default-existing-string-key").setApplicationDataType(String.class).setStorageType(String.class).build();

        item.load();

        assertEquals("stored-value", item.getCurrentValue());
    }

    @Test
    void loadWithNullDefaultUsesStoredIntegerWhenPresent() {
        preferences.putInt("null-default-existing-integer-key", 123);

        StorableConfigurationItem<String, String, Integer, Integer> item = builderFactory.<String, String, Integer, Integer>getInstance().setItemID("null-default-existing-integer-key").setApplicationDataType(Integer.class).setStorageType(Integer.class).build();

        item.load();

        assertEquals(123, item.getCurrentValue());
    }

    @Test
    void loadWithNullDefaultUsesStoredBooleanWhenPresent() {
        preferences.putBoolean("null-default-existing-boolean-key", true);

        StorableConfigurationItem<String, String, Boolean, Boolean> item = builderFactory.<String, String, Boolean, Boolean>getInstance().setItemID("null-default-existing-boolean-key").setApplicationDataType(Boolean.class).setStorageType(Boolean.class).build();

        item.load();
        assertNotNull(item.getCurrentValue());
        assertTrue(item.getCurrentValue());
    }

    @Test
    void loadWithNullDefaultUsesStoredLongWhenPresent() {
        preferences.putLong("null-default-existing-long-key", 123L);

        StorableConfigurationItem<String, String, Long, Long> item = builderFactory.<String, String, Long, Long>getInstance().setItemID("null-default-existing-long-key").setApplicationDataType(Long.class).setStorageType(Long.class).build();

        item.load();

        assertEquals(123L, item.getCurrentValue());
    }

    @Test
    void loadWithNullDefaultUsesStoredFloatWhenPresent() {
        preferences.putFloat("null-default-existing-float-key", 123.5F);

        StorableConfigurationItem<String, String, Float, Float> item = builderFactory.<String, String, Float, Float>getInstance().setItemID("null-default-existing-float-key").setApplicationDataType(Float.class).setStorageType(Float.class).build();

        item.load();

        assertEquals(123.5F, item.getCurrentValue());
    }

    @Test
    void loadWithNullDefaultUsesStoredDoubleWhenPresent() {
        preferences.putDouble("null-default-existing-double-key", 123.5D);

        StorableConfigurationItem<String, String, Double, Double> item = builderFactory.<String, String, Double, Double>getInstance().setItemID("null-default-existing-double-key").setApplicationDataType(Double.class).setStorageType(Double.class).build();

        item.load();

        assertEquals(123.5D, item.getCurrentValue());
    }

    @Test
    void loadWithNullDefaultUsesStoredByteArrayWhenPresent() {
        preferences.putByteArray("null-default-existing-byte-array-key", new byte[]{1, 2, 3});

        StorableConfigurationItem<String, String, byte[], byte[]> item = builderFactory.<String, String, byte[], byte[]>getInstance().setItemID("null-default-existing-byte-array-key").setApplicationDataType(byte[].class).setStorageType(byte[].class).build();

        item.load();

        assertArrayEquals(new byte[]{1, 2, 3}, item.getCurrentValue());
    }

    @Test
    void savingNullCurrentValueDoesNotOverwriteExistingStoredValue() {
        StorableConfigurationItem<String, String, String, String> item = createSameTypeItem("null-current-value-key", String.class, "default");

        item.setCurrentValue("stored-value");
        item.save();

        item.setCurrentValue(null);
        item.save();

        item.load();

        assertEquals("stored-value", item.getCurrentValue());
    }

    @ParameterizedTest
    @MethodSource("invalidStoredPrimitiveValues")
    <A> void invalidStoredPrimitiveValueFallsBackToDefaultValue(String key, Class<A> valueType, A defaultValue) {
        preferences.put(key, "not-a-valid-" + valueType.getSimpleName());

        StorableConfigurationItem<String, String, A, A> item = createSameTypeItem(key, valueType, defaultValue);
        item.load();

        assertEquals(defaultValue, item.getCurrentValue());
    }

    @Test
    void invalidStoredBooleanValueFallsBackToFalseWhenDefaultValueIsNull() {
        preferences.put("invalid-boolean-null-default-key", "not-a-boolean");

        StorableConfigurationItem<String, String, Boolean, Boolean> item = builderFactory.<String, String, Boolean, Boolean>getInstance().setItemID("invalid-boolean-null-default-key").setApplicationDataType(Boolean.class).setStorageType(Boolean.class).build();

        item.load();

        assertNotNull(item.getCurrentValue());
        assertFalse(item.getCurrentValue());
    }

    @Test
    void unsupportedStorageTypeIsRejectedByBuilder() {
        StorableConfigurationItemBuilder<String, String, Byte, Byte> builder = builderFactory.<String, String, Byte, Byte>getInstance().setItemID("unsupported-byte-key").setApplicationDataType(Byte.class).setStorageType(Byte.class);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void unsupportedRuntimeStoredValueThrowsWhenStoring() {
        PreferencesStorageProvider provider = new PreferencesStorageProvider(preferences);
        UnsupportedValueConfigurationItem item = new UnsupportedValueConfigurationItem();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> provider.storeConfigurationItem(item));

        assertEquals("Unsupported type: 'java.lang.Object'", exception.getMessage());
    }

    @Test
    void testCustomApplicationDataValueClass() {
        StorableConfigurationItemBuilder<IdentityEnum, String, CustomConfigItem, String> builder;
        StorableConfigurationItem<IdentityEnum, String, CustomConfigItem, String> item;

        UUID uuid = UUID.randomUUID();
        LocalDate localDate = LocalDate.of(2026, Month.AUGUST, 11);
        String description = "test";

        builder = builderFactory.getInstance();
        CustomConfigItem customConfigItem = new CustomConfigItem(uuid, localDate, description);

        builder.setItemID(IdentityEnum.CUSTOM_CONFIG_ITEM).setApplicationDataType(CustomConfigItem.class).setStorageType(String.class).setToDataType(CustomConfigItem.TO_DATA_TYPE).setToStorageType(CustomConfigItem.TO_STORAGE_TYPE).build();


        item = assertDoesNotThrow(builder::build);
        assertNotNull(item);

        item.setCurrentValue(customConfigItem);
        item.save();
        item.load();
        assertNotNull(item.getCurrentValue());

        // The object should not be the same, as the load() call creates a new instance from the storage
        // THis behavior can be changed by overriding the hashcode and equals methods.
        assertNotEquals(customConfigItem, item.getCurrentValue());

        assertEquals(customConfigItem.description, item.getCurrentValue().description);
        assertEquals(customConfigItem.id, item.getCurrentValue().id);
        assertEquals(customConfigItem.inceptionDate, item.getCurrentValue().inceptionDate);
    }

    private <A> StorableConfigurationItem<String, String, A, A> createSameTypeItem(String key, Class<A> valueType, A defaultValue) {
        StorableConfigurationItemBuilder<String, String, A, A> builder = builderFactory.<String, String, A, A>getInstance().setItemID(key).setApplicationDataType(valueType).setStorageType(valueType);

        if (defaultValue != null) {
            builder.setDefaultValue(defaultValue);
        }

        return builder.build();
    }

    private static void assertValueEquals(Object expected, Object actual) {
        if (expected instanceof byte[] expectedBytes) {
            assertArrayEquals(expectedBytes, (byte[]) actual);
            return;
        }

        assertEquals(expected, actual);
    }


    private static final class UnsupportedValueConfigurationItem implements StorableConfigurationItem<String, String, Object, Object> {

        private final Object value = new Object();

        @Override
        public ConfigurationItemAccessor<String, String, Object, Object> configurationItemAccessor() {
            return new ConfigurationItemAccessor<>() {
                @Override
                public Class<String> intKeyType() {
                    return String.class;
                }

                @Override
                public Class<String> extKeyType() {
                    return String.class;
                }

                @Override
                public Class<Object> applicationDataType() {
                    return Object.class;
                }

                @Override
                public Class<Object> storageType() {
                    return Object.class;
                }

                @Override
                public java.util.function.Function<String, String> toExtKey() {
                    return key -> key;
                }

                @Override
                public java.util.function.Function<Object, Object> toDataType() {
                    return value1 -> value1;
                }

                @Override
                public java.util.function.Function<Object, Object> toStorageType() {
                    return value2 -> value2;
                }
            };
        }

        @Override
        public @NonNull StorageProvider storageProvider() {
            //noinspection DataFlowIssue - testing only
            return null;
        }

        @Override
        public @NotNull String getKey() {
            return "unsupported-runtime-value-key";
        }

        @Override
        public void setCurrentValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getCurrentValue() {
            return value;
        }

        @Override
        public Object getDefaultValue() {
            return null;
        }

        @Override
        public boolean hasUnsavedChanges() {
            return false;
        }

        @Override
        public void clearUnsavedChanges() {
            // Intentionally empty - it's test code
        }
    }

    private enum IdentityEnum {
        CUSTOM_CONFIG_ITEM
    }

    @SuppressWarnings("ClassCanBeRecord") // We intentionally use a class here to test on non-equality
    private static class CustomConfigItem {
        private static final String FIELD_SEPARATOR = "\t";

        private final UUID id;
        private final LocalDate inceptionDate;
        private final String description;

        private CustomConfigItem(UUID id, LocalDate inceptionDate, String description) {
            this.id = id;
            this.inceptionDate = inceptionDate;
            this.description = description;
        }

        private static final Function<CustomConfigItem, String> TO_STORAGE_TYPE = customConfigItem -> {
            if (customConfigItem == null) {
                return null;
            }
            return customConfigItem.id + FIELD_SEPARATOR + customConfigItem.inceptionDate + FIELD_SEPARATOR + customConfigItem.description;
        };

        private static final Function<String, CustomConfigItem> TO_DATA_TYPE = string -> {
            if (string == null) {
                return null;
            }
            String[] strings = string.split(FIELD_SEPARATOR);
            UUID newId = UUID.fromString(strings[0]);
            LocalDate newDate = LocalDate.parse(strings[1]);
            return new CustomConfigItem(newId, newDate, strings[2]);
        };
    }
}