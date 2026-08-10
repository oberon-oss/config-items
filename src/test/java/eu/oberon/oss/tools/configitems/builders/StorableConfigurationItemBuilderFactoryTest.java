package eu.oberon.oss.tools.configitems.builders;

import eu.oberon.oss.tools.configitems.items.StorableConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.StorageProvider;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class StorableConfigurationItemBuilderFactoryTest {

    @Test
    void buildRejectsMissingExternalKeyType() {
        StorableConfigurationItemBuilder<String, String, Integer, Integer> builder =
                new DefaultStorableConfigurationItemBuilder<String, String, Integer, Integer>()
                        .setStorageProvider(new AllowingStorageProvider())
                        .setItemID("missing-external-key-type-key")
                        .setDefaultValue(1);

        NullPointerException exception = assertThrows(NullPointerException.class, builder::build);

        assertEquals("External Key data type must not be null", exception.getMessage());
    }

    @Test
    void buildRejectsMissingStorageProvider() {
        StorableConfigurationItemBuilder<String, String, Integer, Integer> builder =
                new DefaultStorableConfigurationItemBuilder<String, String, Integer, Integer>()
                        .setExtKeyType(String.class)
                        .setItemID("missing-storage-provider-key")
                        .setDefaultValue(1);

        NullPointerException exception = assertThrows(NullPointerException.class, builder::build);

        assertEquals("Storage provider must not be null", exception.getMessage());
    }

    @Test
    void buildRejectsItemIdThatDoesNotMatchInternalKeyType() throws NoSuchFieldException, IllegalAccessException {
        StorableConfigurationItemBuilder<String, String, Integer, Integer> builder =
                new DefaultStorableConfigurationItemBuilder<String, String, Integer, Integer>()
                        .setStorageProvider(new AllowingStorageProvider())
                        .setExtKeyType(String.class)
                        .setItemID("string-item-id")
                        .setDefaultValue(1);

        Field intKeyTypeField = DefaultStorableConfigurationItemBuilder.class.getDeclaredField("intKeyType");
        intKeyTypeField.setAccessible(true);
        intKeyTypeField.set(builder, Integer.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertEquals(
                "Item ID string-item-id is not an instance of internal key type class java.lang.Integer",
                exception.getMessage()
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void buildRejectsDefaultValueThatDoesNotMatchApplicationDataType() {
        StorableConfigurationItemBuilder<String, String, Object, Integer> builder =
                new DefaultStorableConfigurationItemBuilder<String, String, Object, Integer>()
                        .setStorageProvider(new AllowingStorageProvider())
                        .setExtKeyType(String.class)
                        .setItemID("wrong-default-value-application-type-key")
                        .setDefaultValue(123)
                        .setApplicationDataType((Class<Object>) (Class<?>) String.class)
                        .setStorageType(Integer.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertEquals(
                "Default value 123 is not an instance of application data type class java.lang.String",
                exception.getMessage()
        );
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void buildRejectsConvertedDefaultValueThatDoesNotMatchStorageType() {
        StorableConfigurationItemBuilder<String, String, Object, Object> builder =
                new DefaultStorableConfigurationItemBuilder<String, String, Object, Object>()
                        .setStorageProvider(new AllowingStorageProvider())
                        .setExtKeyType(String.class)
                        .setItemID("wrong-converted-default-value-storage-type-key")
                        .setDefaultValue(123)
                        .setApplicationDataType(Object.class)
                        .setStorageType((Class<Object>) (Class) String.class)
                        .setToDataType(value -> value)
                        .setToStorageType(value -> 123);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertEquals(
                "Default value 123 is not an instance of storage data type class java.lang.String",
                exception.getMessage()
        );
    }

    @Test
    void buildRejectsMissingToStorageTypeWhenAutoGenerationCannotCreateConverter() {
        StorableConfigurationItemBuilder<String, String, Object, Integer> builder =
                new DefaultStorableConfigurationItemBuilder<String, String, Object, Integer>()
                        .setStorageProvider(new AllowingStorageProvider())
                        .setExtKeyType(String.class)
                        .setItemID("missing-to-storage-type-key")
                        .setApplicationDataType(Object.class)
                        .setStorageType(Integer.class)
                        .setToDataType(value -> value);

        NullPointerException exception = assertThrows(NullPointerException.class, builder::build);

        assertEquals("'toStorageType()' function must not be null", exception.getMessage());
    }

    @Test
    void buildUsesAutoGeneratedConverterFromStringStorageType() {
        StorableConfigurationItem<String, String, Integer, String> item =
                new DefaultStorableConfigurationItemBuilder<String, String, Integer, String>()
                        .setStorageProvider(new AllowingStorageProvider())
                        .setExtKeyType(String.class)
                        .setItemID("string-to-integer-auto-converter-key")
                        .setApplicationDataType(Integer.class)
                        .setStorageType(String.class)
                        .setDefaultValue(123)
                        .build();

        assertEquals("string-to-integer-auto-converter-key", item.getKey());
        assertEquals(123, item.getCurrentValue());
    }

    @Test
    void buildAllowsNullDefaultValueWhenTypesAreSetExplicitly() {
        StorableConfigurationItem<String, String, Integer, Integer> item =
                new DefaultStorableConfigurationItemBuilder<String, String, Integer, Integer>()
                        .setStorageProvider(new AllowingStorageProvider())
                        .setExtKeyType(String.class)
                        .setItemID("null-default-value-key")
                        .setApplicationDataType(Integer.class)
                        .setStorageType(Integer.class)
                        .build();

        assertEquals("null-default-value-key", item.getKey());
        assertNull(item.getDefaultValue());
        assertNull(item.getCurrentValue());
    }

    private static final class AllowingStorageProvider implements StorageProvider {

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
            return true;
        }

        @Override
        public boolean isStorageClassTypeAllowed(Class<?> valueClass) {
            return true;
        }
    }
}