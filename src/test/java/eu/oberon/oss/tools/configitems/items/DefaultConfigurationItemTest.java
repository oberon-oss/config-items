package eu.oberon.oss.tools.configitems.items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;

class DefaultConfigurationItemTest {

    @Test
    void hasUnsavedChangesReturnsFalseInitially() {
        DefaultConfigurationItem<String, String, String> item = new DefaultConfigurationItem<>("key", "default", String.class, String.class, Function.identity(), Function.identity());
        assertFalse(item.hasUnsavedChanges());
    }

    @Test
    void hasUnsavedChangesReturnsTrueAfterValueChange() {
        DefaultConfigurationItem<String, String, String> item = new DefaultConfigurationItem<>("key", "default", String.class, String.class, Function.identity(), Function.identity());
        item.setCurrentValue("new");
        assertTrue(item.hasUnsavedChanges());
    }

    @Test
    void hasUnsavedChangesReturnsFalseAfterSettingSameValue() {
        DefaultConfigurationItem<String, String, String> item = new DefaultConfigurationItem<>("key", "default", String.class, String.class, Function.identity(), Function.identity());
        item.setCurrentValue("default");
        assertFalse(item.hasUnsavedChanges());
    }

    @Test
    void clearUnsavedChangesResetsChangedFlag() {
        DefaultConfigurationItem<String, String, String> item = new DefaultConfigurationItem<>("key", "default", String.class, String.class, Function.identity(), Function.identity());
        item.setCurrentValue("new");
        assertTrue(item.hasUnsavedChanges());
        
        item.clearUnsavedChanges();
        assertFalse(item.hasUnsavedChanges());
    }

    @Test
    void getKeyReturnsConstructorValue() {
        DefaultConfigurationItem<String, String, String> item = new DefaultConfigurationItem<>("key", "default", String.class, String.class, Function.identity(), Function.identity());
        assertEquals("key", item.getKey());
    }

    @Test
    void getCurrentValueReturnsConstructorDefaultInitially() {
        DefaultConfigurationItem<String, String, String> item = new DefaultConfigurationItem<>("key", "default", String.class, String.class, Function.identity(), Function.identity());
        assertEquals("default", item.getCurrentValue());
    }

    @Test
    void getDefaultValueReturnsConstructorDefault() {
        DefaultConfigurationItem<String, String, String> item = new DefaultConfigurationItem<>("key", "default", String.class, String.class, Function.identity(), Function.identity());
        assertEquals("default", item.getDefaultValue());
    }
}
