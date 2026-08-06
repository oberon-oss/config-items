package eu.oberon.oss.tools.configitems;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultConfigurationItemTest {

    @Mock
    private Preferences preferences;

    @Test
    void testGetInstance_String() {
        ConfigurationItem<String> item = DefaultConfigurationItem.getInstance("test", String.class, "default");
        assertThat(item.getItemName()).isEqualTo("test");
        assertThat(item.getConfigItemValue()).isNull();

        item.setConfigItemValue("newVal");
        assertThat(item.getConfigItemValue()).isEqualTo("newVal");
    }

    @Test
    void testGetInstance_Integer() {
        ConfigurationItem<Integer> item = DefaultConfigurationItem.getInstance("test", Integer.class, 42);
        assertThat(item.getItemName()).isEqualTo("test");
    }

    @Test
    void testGetInstance_UnsupportedType() {
        assertThatThrownBy(() -> DefaultConfigurationItem.getInstance("test", Object.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not an allowed type");
    }

    @Test
    void testGetInstance_DefaultValueDerivedType() {
        ConfigurationItem<String> item = DefaultConfigurationItem.getInstance("test", "default");
        assertThat(item.getItemName()).isEqualTo("test");
    }

    @Test
    void testGetEnumInstance() {
        ConfigurationItem<ConfigTestEnum> item = DefaultConfigurationItem.getEnumInstance("test", ConfigTestEnum.class, ConfigTestEnum.VALUE_A);
        item.setConfigItemValue(ConfigTestEnum.VALUE_B);

        when(preferences.get("test", null)).thenReturn("VALUE_B");
        when(preferences.get("test", "VALUE_A")).thenReturn("VALUE_B");

        item.store(preferences);
        verify(preferences).put("test", "VALUE_B");

        item.load(preferences);
        assertThat(item.getConfigItemValue()).isEqualTo(ConfigTestEnum.VALUE_B);
    }

    @Test
    void testGetEnumInstance_NoDefault() {
        ConfigurationItem<ConfigTestEnum> item = DefaultConfigurationItem.getEnumInstance("test", ConfigTestEnum.class);
        assertThat(item.getItemName()).isEqualTo("test");

        when(preferences.get("test", null)).thenReturn("VALUE_C");

        item.load(preferences);
        assertThat(item.getConfigItemValue()).isEqualTo(ConfigTestEnum.VALUE_C);
    }

    @Test
    void testGetEnumInstanceFromDefaultValue() {
        ConfigurationItem<ConfigTestEnum> item = DefaultConfigurationItem.getInstance("test", ConfigTestEnum.VALUE_A);
        assertThat(item.getItemName()).isEqualTo("test");

        when(preferences.get("test", null)).thenReturn("VALUE_B");
        when(preferences.get("test", "VALUE_A")).thenReturn("VALUE_B");

        item.load(preferences);
        assertThat(item.getConfigItemValue()).isEqualTo(ConfigTestEnum.VALUE_B);
    }

    @Test
    void testGetConvertedInstance_LocalDateList() {
        List<LocalDate> defaultDates = List.of(LocalDate.of(2023, Month.JANUARY, 1));
        List<LocalDate> newDates = List.of(LocalDate.of(2024, Month.JANUARY, 1), LocalDate.of(2024, Month.DECEMBER, 31));
        String storedValue = "2024-01-01\t2024-12-31";
        String defaultStoredValue = "2023-01-01";

        ConfigurationItem<List<LocalDate>> item = DefaultConfigurationItem.getConvertedInstance(
                "dates",
                String.class,
                defaultDates,
                list -> list.stream().map(LocalDate::toString).collect(Collectors.joining("\t")),
                str -> Arrays.stream(str.split("\t")).map(LocalDate::parse).toList()
        );

        item.setConfigItemValue(newDates);
        item.store(preferences);
        verify(preferences).put("dates", storedValue);

        when(preferences.get("dates", null)).thenReturn(storedValue);
        when(preferences.get("dates", defaultStoredValue)).thenReturn(storedValue);

        item.load(preferences);
        assertThat(item.getConfigItemValue()).containsExactlyElementsOf(newDates);
    }

    @Test
    void testGetStringBackedInstance() {
        ConfigurationItem<Integer> item = DefaultConfigurationItem.getStringBackedInstance("test", 10, Integer::valueOf);
        item.setConfigItemValue(20);

        item.store(preferences);
        verify(preferences).put("test", "20");

        when(preferences.get("test", null)).thenReturn("30");
        when(preferences.get("test", "10")).thenReturn("30");
        item.load(preferences);
        assertThat(item.getConfigItemValue()).isEqualTo(30);
    }

    @Test
    void testLoad_DefaultsIfNotExist() {
        ConfigurationItem<String> item = DefaultConfigurationItem.getInstance("test", String.class, "default");
        when(preferences.get("test", null)).thenReturn(null);

        item.load(preferences);
        assertThat(item.getConfigItemValue()).isEqualTo("default");
    }

    @Test
    void testStoreAndLoad_AllTypes() {
        // String
        ConfigurationItem<String> stringItem = DefaultConfigurationItem.getInstance("s", "def");
        stringItem.setConfigItemValue("val");
        stringItem.store(preferences);
        verify(preferences).put("s", "val");
        when(preferences.get("s", null)).thenReturn("val");
        when(preferences.get("s", "def")).thenReturn("val");
        stringItem.load(preferences);
        assertThat(stringItem.getConfigItemValue()).isEqualTo("val");

        // Integer
        ConfigurationItem<Integer> intItem = DefaultConfigurationItem.getInstance("i", 1);
        intItem.setConfigItemValue(2);
        intItem.store(preferences);
        verify(preferences).putInt("i", 2);
        when(preferences.get("i", null)).thenReturn("anything");
        when(preferences.getInt("i", 1)).thenReturn(2);
        intItem.load(preferences);
        assertThat(intItem.getConfigItemValue()).isEqualTo(2);

        // Long
        ConfigurationItem<Long> longItem = DefaultConfigurationItem.getInstance("l", 1L);
        longItem.setConfigItemValue(2L);
        longItem.store(preferences);
        verify(preferences).putLong("l", 2L);
        when(preferences.get("l", null)).thenReturn("anything");
        when(preferences.getLong("l", 1L)).thenReturn(2L);
        longItem.load(preferences);
        assertThat(longItem.getConfigItemValue()).isEqualTo(2L);

        // Boolean
        ConfigurationItem<Boolean> boolItem = DefaultConfigurationItem.getInstance("b", false);
        boolItem.setConfigItemValue(true);
        boolItem.store(preferences);
        verify(preferences).putBoolean("b", true);
        when(preferences.get("b", null)).thenReturn("anything");
        when(preferences.getBoolean("b", false)).thenReturn(true);
        boolItem.load(preferences);
        assertThat(boolItem.getConfigItemValue()).isTrue();

        // Float
        ConfigurationItem<Float> floatItem = DefaultConfigurationItem.getInstance("f", 1.0f);
        floatItem.setConfigItemValue(2.0f);
        floatItem.store(preferences);
        verify(preferences).putFloat("f", 2.0f);
        when(preferences.get("f", null)).thenReturn("anything");
        when(preferences.getFloat("f", 1.0f)).thenReturn(2.0f);
        floatItem.load(preferences);
        assertThat(floatItem.getConfigItemValue()).isEqualTo(2.0f);

        // Double
        ConfigurationItem<Double> doubleItem = DefaultConfigurationItem.getInstance("d", 1.0);
        doubleItem.setConfigItemValue(2.0);
        doubleItem.store(preferences);
        verify(preferences).putDouble("d", 2.0);
        when(preferences.get("d", null)).thenReturn("anything");
        when(preferences.getDouble("d", 1.0)).thenReturn(2.0);
        doubleItem.load(preferences);
        assertThat(doubleItem.getConfigItemValue()).isEqualTo(2.0);

        // byte[]
        byte[] def = new byte[]{1};
        byte[] val = new byte[]{2};
        ConfigurationItem<byte[]> byteItem = DefaultConfigurationItem.getInstance("bytes", byte[].class, def);
        byteItem.setConfigItemValue(val);
        byteItem.store(preferences);
        verify(preferences).putByteArray("bytes", val);
        when(preferences.get("bytes", null)).thenReturn("anything");
        when(preferences.getByteArray("bytes", def)).thenReturn(val);
        byteItem.load(preferences);
        assertThat(byteItem.getConfigItemValue()).isEqualTo(val);
    }

    @Test
    void testStore_NullValue() {
        ConfigurationItem<String> item = DefaultConfigurationItem.getInstance("test", String.class);
        item.setConfigItemValue(null);
        item.store(preferences);
        verify(preferences, never()).put(anyString(), anyString());
    }

    @Test
    void testLoad_NullValueInPreferences() {
        ConfigurationItem<String> item = DefaultConfigurationItem.getInstance("test", String.class);
        when(preferences.get("test", null)).thenReturn("exists");
        when(preferences.get("test", null)).thenReturn(null);
        
        item.load(preferences);
        assertThat(item.getConfigItemValue()).isNull();
    }

    @Test
    void testPrivateConstructorThrowsException() throws Exception {
        //noinspection rawtypes
        java.lang.reflect.Constructor<DefaultConfigurationItem> constructor = DefaultConfigurationItem.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(java.lang.reflect.InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

}
