package eu.oberon.oss.tools.configitems.items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultConfigurationItemTest {

    @Test
    void hasUnsavedChangesReturnsFalseInitially() {
        DefaultConfigurationItem<String, String> item = new DefaultConfigurationItem<>("key", "default");
        assertFalse(item.hasUnsavedChanges());
    }

    @Test
    void hasUnsavedChangesReturnsTrueAfterValueChange() {
        DefaultConfigurationItem<String, String> item = new DefaultConfigurationItem<>("key", "default");
        item.setCurrentValue("new");
        assertTrue(item.hasUnsavedChanges());
    }

    @Test
    void hasUnsavedChangesReturnsFalseAfterSettingSameValue() {
        DefaultConfigurationItem<String, String> item = new DefaultConfigurationItem<>("key", "default");
        item.setCurrentValue("default");
        assertFalse(item.hasUnsavedChanges());
    }

    @Test
    void clearUnsavedChangesResetsChangedFlag() {
        DefaultConfigurationItem<String, String> item = new DefaultConfigurationItem<>("key", "default");
        item.setCurrentValue("new");
        assertTrue(item.hasUnsavedChanges());
        
        item.clearUnsavedChanges();
        assertFalse(item.hasUnsavedChanges());
    }

    @Test
    void getKeyReturnsConstructorValue() {
        DefaultConfigurationItem<String, String> item = new DefaultConfigurationItem<>("key", "default");
        assertEquals("key", item.getKey());
    }

    @Test
    void getCurrentValueReturnsConstructorDefaultInitially() {
        DefaultConfigurationItem<String, String> item = new DefaultConfigurationItem<>("key", "default");
        assertEquals("default", item.getCurrentValue());
    }

    @Test
    void getDefaultValueReturnsConstructorDefault() {
        DefaultConfigurationItem<String, String> item = new DefaultConfigurationItem<>("key", "default");
        assertEquals("default", item.getDefaultValue());
    }
}
