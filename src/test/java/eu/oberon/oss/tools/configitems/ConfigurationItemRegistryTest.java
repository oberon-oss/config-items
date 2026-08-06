package eu.oberon.oss.tools.configitems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.prefs.Preferences;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationItemRegistryTest {

    @Mock
    private Preferences preferences;

    private ConfigurationItemRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ConfigurationItemRegistry(preferences);
    }

    @Test
    void testRegisterAndGetSetItemValue() {
        ConfigurationItem<String> item = DefaultConfigurationItem.getInstance("test", String.class, "default");
        registry.register(item);

        String value = registry.getItemValue("test");
        assertThat(value).isNull();

        registry.setItemValue("test", "newVal");
        String newValue = registry.getItemValue("test");
        assertThat(newValue).isEqualTo("newVal");
        assertThat(item.getConfigItemValue()).isEqualTo("newVal");
    }

    @Test
    void testRegisterDuplicate_ThrowsException() {
        ConfigurationItem<String> item1 = DefaultConfigurationItem.getInstance("test", String.class, "def1");
        ConfigurationItem<String> item2 = DefaultConfigurationItem.getInstance("test", String.class, "def2");

        registry.register(item1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> registry.register(item2));
        assertThat(exception.getMessage()).contains("Item with name 'test' already exists");
    }

    @Test
    void testRegisterDuplicate_Replace() {
        ConfigurationItem<String> item1 = DefaultConfigurationItem.getInstance("test", String.class, "def1");
        ConfigurationItem<String> item2 = DefaultConfigurationItem.getInstance("test", String.class, "def2");

        registry.register(item1);
        registry.register(item2, true);

        // Verify that setting value updates item2, not item1
        registry.setItemValue("test", "val2");
        assertThat(item2.getConfigItemValue()).isEqualTo("val2");
        assertThat(item1.getConfigItemValue()).isNotEqualTo("val2");
    }

    @Test
    void testRegister_NullItem() {
        //noinspection DataFlowIssue passing null is intentional
        assertThrows(RuntimeException.class, () -> registry.register(null));
    }

    @Test
    void testLoadData() {
        ConfigurationItem<String> item1 = DefaultConfigurationItem.getInstance("item1", String.class, "def1");
        ConfigurationItem<Integer> item2 = DefaultConfigurationItem.getInstance("item2", Integer.class, 2);

        registry.register(item1);
        registry.register(item2);

        when(preferences.get("item1", null)).thenReturn("val1");
        when(preferences.get("item1", "def1")).thenReturn("val1");
        when(preferences.get("item2", null)).thenReturn("anything");
        when(preferences.getInt("item2", 2)).thenReturn(10);

        registry.loadData();

        String val1 = registry.getItemValue("item1");
        Integer val2 = registry.getItemValue("item2");
        assertThat(val1).isEqualTo("val1");
        assertThat(val2).isEqualTo(10);
    }

    @Test
    void testStoreData() {
        ConfigurationItem<String> item = DefaultConfigurationItem.getInstance("test", String.class, "def");
        registry.register(item);
        registry.setItemValue("test", "val");

        registry.storeData();

        verify(preferences).put("test", "val");
    }

    @Test
    void testGetItemValue_NotFound() {
        Object value = registry.getItemValue("nonexistent");
        assertThat(value).isNull();
    }

    @Test
    void testSetItemValue_NotFound() {
        assertThrows(NullPointerException.class, () -> registry.setItemValue("nonexistent", "value"));
    }
}
