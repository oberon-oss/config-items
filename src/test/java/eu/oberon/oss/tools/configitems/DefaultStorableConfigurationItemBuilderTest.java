package eu.oberon.oss.tools.configitems;

import eu.oberon.oss.tools.configitems.builders.StorableConfigurationItemBuilder;
import eu.oberon.oss.tools.configitems.builders.StorableConfigurationItemBuilderFactory;
import eu.oberon.oss.tools.configitems.storage.PreferencesStorageProvider;
import eu.oberon.oss.tools.configitems.storage.StorableConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.StorageProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static eu.oberon.oss.tools.configitems.ConfigTestEnum.VALUE_A;
import static eu.oberon.oss.tools.configitems.ConfigTestEnum.VALUE_B;
import static org.junit.jupiter.api.Assertions.*;

class DefaultStorableConfigurationItemBuilderTest {

    private Preferences preferences;
    private PreferencesStorageProvider storageProvider;
    private StorableConfigurationItemBuilderFactory builderFactory;
    private StorableConfigurationItemBuilder<ConfigTestEnum, String, Integer, String> builder;

    @BeforeEach
    void setUp() throws BackingStoreException {
        preferences = Preferences.userRoot().node("/eu/oberon/oss/tools/configitems/builder-test/" + UUID.randomUUID());
        preferences.clear();

        storageProvider = new PreferencesStorageProvider(preferences);
        builderFactory = StorableConfigurationItemBuilderFactory.create(storageProvider);
        builder = builderFactory.getInstance();
    }

    @AfterEach
    void tearDown() throws BackingStoreException {
        preferences.removeNode();
    }

    @Test
    void basicTest() {
        StorableConfigurationItem<ConfigTestEnum, String, Integer, String> test = builder
                .setItemID(VALUE_B)
                .setDefaultValue(1)
                .build();

        assertNotNull(test);

        assertDoesNotThrow(test::save);
        assertDoesNotThrow(test::load);

        assertEquals(1, test.getCurrentValue());
        assertEquals(VALUE_B, test.getKey());
    }

    @Test
    void buildInfersInternalKeyTypeFromItemId() {
        StorableConfigurationItem<ConfigTestEnum, String, Integer, String> item = builderFactory
                .<ConfigTestEnum, String, Integer, String>getInstance()
                .setItemID(VALUE_A)
                .setApplicationDataType(Integer.class)
                .setStorageType(String.class)
                .setToStorageType(String::valueOf)
                .setToDataType(Integer::valueOf)
                .build();

        assertEquals(VALUE_A, item.getKey());
    }

    @Test
    void setItemIDDoesNotReplaceAlreadyInferredInternalKeyType() {
        StorableConfigurationItemBuilder<Object, String, Integer, Integer> testBuilder = builderFactory
                .<Object, String, Integer, Integer>getInstance()
                .setItemID("string-key")
                .setItemID(123)
                .setDefaultValue(1)
                .setToExtKey(Object::toString);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, testBuilder::build);

        assertEquals(
                "Item ID 123 is not an instance of internal key type class java.lang.String",
                exception.getMessage()
        );
    }

    @Test
    void buildInfersApplicationAndStorageTypesFromDefaultValue() {
        StorableConfigurationItem<String, String, Integer, Integer> item = builderFactory
                .<String, String, Integer, Integer>getInstance()
                .setItemID("inferred-default-value-key")
                .setDefaultValue(123)
                .build();

        assertEquals("inferred-default-value-key", item.getKey());
        assertEquals(123, item.getDefaultValue());
        assertEquals(123, item.getCurrentValue());
    }

    @Test
    void setDefaultValueWithNullDoesNotInferApplicationOrStorageTypes() {
        StorableConfigurationItem<String, String, Integer, Integer> item = builderFactory
                .<String, String, Integer, Integer>getInstance()
                .setItemID("null-default-value-key")
                .setDefaultValue(null)
                .setApplicationDataType(Integer.class)
                .setStorageType(Integer.class)
                .build();

        assertEquals("null-default-value-key", item.getKey());
        assertNull(item.getDefaultValue());
        assertNull(item.getCurrentValue());
        assertEquals(Integer.class, item.configurationItemAccessor().applicationDataType());
        assertEquals(Integer.class, item.configurationItemAccessor().storageType());
    }

    @Test
    void setDefaultValueDoesNotReplaceAlreadyConfiguredStorageType() {
        StorableConfigurationItem<String, String, Integer, String> item = builderFactory
                .<String, String, Integer, String>getInstance()
                .setItemID("preconfigured-storage-type-key")
                .setStorageType(String.class)
                .setDefaultValue(123)
                .build();

        assertEquals("preconfigured-storage-type-key", item.getKey());
        assertEquals(123, item.getDefaultValue());
        assertEquals(123, item.getCurrentValue());
        assertEquals(Integer.class, item.configurationItemAccessor().applicationDataType());
        assertEquals(String.class, item.configurationItemAccessor().storageType());
    }

    @Test
    void buildUsesAutoGeneratedIdentityConvertersForMatchingTypes() {
        StorableConfigurationItem<String, String, Integer, Integer> item = builderFactory
                .<String, String, Integer, Integer>getInstance()
                .setItemID("identity-converter-key")
                .setDefaultValue(1)
                .build();

        item.setCurrentValue(42);
        item.save();

        item.setCurrentValue(1);
        item.load();

        assertEquals(42, item.getCurrentValue());
    }

    @Test
    void buildUsesAutoGeneratedStringConvertersForSupportedDataTypes() {
        StorableConfigurationItem<String, String, Integer, String> item = builderFactory
                .<String, String, Integer, String>getInstance()
                .setItemID("integer-string-converter-key")
                .setDefaultValue(10)
                .setStorageType(String.class)
                .build();

        item.setCurrentValue(99);
        item.save();

        assertEquals("99", preferences.get("integer-string-converter-key", null));

        item.setCurrentValue(10);
        item.load();

        assertEquals(99, item.getCurrentValue());
    }

    @Test
    void buildUsesCustomDataConvertersWhenProvided() {
        StorableConfigurationItem<String, String, Integer, String> item = builderFactory
                .<String, String, Integer, String>getInstance()
                .setItemID("custom-data-converter-key")
                .setDefaultValue(5)
                .setStorageType(String.class)
                .setToStorageType(value -> "stored-" + value)
                .setToDataType(value -> Integer.parseInt(value.substring("stored-".length())))
                .build();

        item.setCurrentValue(77);
        item.save();

        assertEquals("stored-77", preferences.get("custom-data-converter-key", null));

        item.setCurrentValue(5);
        item.load();

        assertEquals(77, item.getCurrentValue());
    }

    @Test
    void buildUsesCustomKeyConverterWhenProvided() {
        StorableConfigurationItem<ConfigTestEnum, String, Integer, String> item = builderFactory
                .<ConfigTestEnum, String, Integer, String>getInstance()
                .setItemID(VALUE_B)
                .setDefaultValue(15)
                .setStorageType(String.class)
                .setToExtKey(value -> "custom-" + value.name())
                .setToStorageType(String::valueOf)
                .setToDataType(Integer::valueOf)
                .build();

        item.save();

        assertEquals(VALUE_B, item.getKey());
        assertEquals("15", preferences.get("custom-VALUE_B", null));
    }

    @Test
    void buildRejectsMissingItemId() {
        StorableConfigurationItemBuilder<String, String, Integer, Integer> testBuilder = builderFactory
                .<String, String, Integer, Integer>getInstance()
                .setDefaultValue(1);

        NullPointerException exception = assertThrows(NullPointerException.class, testBuilder::build);

        assertEquals("Parameter: 'itemID'", exception.getMessage());
    }

    @Test
    void buildRejectsMissingExternalKeyType() {
        StorableConfigurationItemBuilderFactory factory = StorableConfigurationItemBuilderFactory.create(storageProvider, false);

        StorableConfigurationItemBuilder<String, String, Integer, Integer> testBuilder = factory
                .<String, String, Integer, Integer>getInstance()
                .setItemID("missing-ext-key-type-key")
                .setDefaultValue(1)
                .setToExtKey(value -> value)
                .setToDataType(value -> value)
                .setToStorageType(value -> value);

        assertDoesNotThrow(testBuilder::build);
    }

    @Test
    void buildRejectsMissingApplicationDataTypeWhenDefaultValueIsNull() {
        StorableConfigurationItemBuilder<String, String, Integer, Integer> testBuilder = builderFactory
                .<String, String, Integer, Integer>getInstance()
                .setItemID("missing-application-type-key")
                .setStorageType(Integer.class);

        NullPointerException exception = assertThrows(NullPointerException.class, testBuilder::build);

        assertEquals("Parameter: 'applicationDataType'", exception.getMessage());
    }

    @Test
    void buildRejectsMissingStorageTypeWhenDefaultValueIsNull() {
        StorableConfigurationItemBuilder<String, String, Integer, Integer> testBuilder = builderFactory
                .<String, String, Integer, Integer>getInstance()
                .setItemID("missing-storage-type-key")
                .setApplicationDataType(Integer.class);

        NullPointerException exception = assertThrows(NullPointerException.class, testBuilder::build);

        assertEquals("Parameter: 'storageType'", exception.getMessage());
    }

    @Test
    void buildRejectsUnsupportedStorageType() {
        StorableConfigurationItemBuilder<String, String, Byte, Byte> testBuilder = builderFactory
                .<String, String, Byte, Byte>getInstance()
                .setItemID("unsupported-storage-type-key")
                .setDefaultValue((byte) 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, testBuilder::build);

        assertEquals("Storage type class java.lang.Byte is not supported", exception.getMessage());
    }

    @Test
    void buildRejectsUnsupportedExternalKeyType() {
        StorableConfigurationItemBuilderFactory factory = StorableConfigurationItemBuilderFactory.create(storageProvider, Integer.class);

        StorableConfigurationItemBuilder<String, Integer, Integer, Integer> testBuilder = factory
                .<String, Integer, Integer, Integer>getInstance()
                .setItemID("unsupported-external-key-type-key")
                .setDefaultValue(1)
                .setToExtKey(String::length);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, testBuilder::build);

        assertEquals("External key type class java.lang.Integer is not supported", exception.getMessage());
    }

    @Test
    void buildRejectsMissingDataConvertersWhenAutoGenerationIsDisabled() {
        StorableConfigurationItemBuilderFactory factory = StorableConfigurationItemBuilderFactory.create(storageProvider, false);

        StorableConfigurationItemBuilder<String, String, Integer, String> testBuilder = factory
                .<String, String, Integer, String>getInstance()
                .setItemID("missing-data-converters-key")
                .setDefaultValue(1)
                .setStorageType(String.class)
                .setToExtKey(value -> value);

        NullPointerException exception = assertThrows(NullPointerException.class, testBuilder::build);

        assertEquals("'toDataType()' function must not be null", exception.getMessage());
    }

    @Test
    void buildRejectsMissingKeyConverterWhenAutoGenerationCannotCreateOne() {
        StorableConfigurationItemBuilderFactory factory = StorableConfigurationItemBuilderFactory.create(storageProvider, String.class, false);

        StorableConfigurationItemBuilder<ConfigTestEnum, String, Integer, Integer> testBuilder = factory
                .<ConfigTestEnum, String, Integer, Integer>getInstance()
                .setItemID(VALUE_B)
                .setDefaultValue(1)
                .setToDataType(value -> value)
                .setToStorageType(value -> value);

        NullPointerException exception = assertThrows(NullPointerException.class, testBuilder::build);

        assertEquals("'toExtKey()' function must not be null", exception.getMessage());
    }

    @Test
    void buildRejectsConvertedDefaultValueWithWrongStorageType() {
        StorableConfigurationItemBuilder<String, String, Integer, String> testBuilder = builderFactory
                .<String, String, Integer, String>getInstance()
                .setItemID("wrong-converted-default-value-type-key")
                .setDefaultValue(1)
                .setStorageType(String.class)
                .setToStorageType(value -> (String) (Object) value)
                .setToDataType(Integer::valueOf);

        assertThrows(ClassCastException.class, testBuilder::build);
    }

    @Test
    void buildRejectsUnsupportedStorageProviderDataType() {
        StorageProvider restrictiveStorageProvider = new RestrictiveStorageProvider(false, true);
        StorableConfigurationItemBuilderFactory factory = StorableConfigurationItemBuilderFactory.create(restrictiveStorageProvider);

        StorableConfigurationItemBuilder<String, String, Integer, Integer> testBuilder = factory
                .<String, String, Integer, Integer>getInstance()
                .setItemID("restrictive-storage-provider-data-key")
                .setDefaultValue(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, testBuilder::build);

        assertEquals("Storage type class java.lang.Integer is not supported", exception.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    void buildRejectsDefaultValueThatDoesNotMatchApplicationDataType() {
        StorableConfigurationItemBuilder<String, String, Object, Integer> testBuilder = builderFactory
                .<String, String, Object, Integer>getInstance()
                .setItemID("wrong-default-value-application-type-key")
                .setDefaultValue(123)
                .setApplicationDataType((Class<Object>) (Class<?>) String.class)
                .setStorageType(Integer.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, testBuilder::build);

        assertEquals(
                "Default value 123 is not an instance of application data type class java.lang.String",
                exception.getMessage()
        );
    }


    private record RestrictiveStorageProvider(
            boolean storageClassTypeAllowed,
            boolean keyClassTypeAllowed
    ) implements StorageProvider {

        @Override
        public <I, K, A, P> void storeConfigurationItem(StorableConfigurationItem<I, K, A, P> item) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <I, K, A, P> void loadConfigurationItem(StorableConfigurationItem<I, K, A, P> item) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isKeyClassTypeAllowed(Class<?> keyClass) {
            return keyClassTypeAllowed;
        }

        @Override
        public boolean isStorageClassTypeAllowed(Class<?> valueClass) {
            return storageClassTypeAllowed;
        }
    }
}