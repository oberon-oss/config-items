package eu.oberon.oss.tools.configitems.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationItemKeyTest {

    @Test
    void ofCreatesTypedKey() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);

        assertEquals("test-key", key.id());
        assertEquals(String.class, key.valueType());
    }

    @Test
    void ofRejectsNullId() {
        assertThrows(NullPointerException.class, () -> ConfigurationItemKey.of(null, String.class));
    }

    @Test
    void ofRejectsNullValueType() {
        assertThrows(NullPointerException.class, () -> ConfigurationItemKey.of("test-key", null));
    }

    @Test
    void ofDefaultValueCreatesTypedKeyFromDefaultValueType() {
        ConfigurationItemKey<Integer> key = ConfigurationItemKey.ofDefaultValue("integer-key", 123);

        assertEquals("integer-key", key.id());
        assertEquals(Integer.class, key.valueType());
    }

    @Test
    void ofDefaultValueRejectsNullId() {
        assertThrows(NullPointerException.class, () -> ConfigurationItemKey.ofDefaultValue(null, "default-value"));
    }

    @Test
    void ofDefaultValueRejectsNullDefaultValue() {
        assertThrows(NullPointerException.class, () -> ConfigurationItemKey.ofDefaultValue("test-key", null));
    }

    @Test
    void equalKeysHaveSameIdAndSameValueType() {
        ConfigurationItemKey<String> firstKey = ConfigurationItemKey.of("test-key", String.class);
        ConfigurationItemKey<String> secondKey = ConfigurationItemKey.of("test-key", String.class);

        assertEquals(firstKey, secondKey);
        assertEquals(firstKey.hashCode(), secondKey.hashCode());
    }

    @Test
    void keysWithSameIdButDifferentValueTypeAreNotEqual() {
        ConfigurationItemKey<String> stringKey = ConfigurationItemKey.of("test-key", String.class);
        ConfigurationItemKey<Integer> integerKey = ConfigurationItemKey.of("test-key", Integer.class);

        assertNotEquals(stringKey, integerKey);
    }
}