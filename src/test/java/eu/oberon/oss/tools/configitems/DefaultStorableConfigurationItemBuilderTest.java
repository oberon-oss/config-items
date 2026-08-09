package eu.oberon.oss.tools.configitems;

import eu.oberon.oss.tools.configitems.builders.StorableConfigurationItemBuilder;
import eu.oberon.oss.tools.configitems.items.StorableConfigurationItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static eu.oberon.oss.tools.configitems.ConfigTestEnum.VALUE_B;
import static eu.oberon.oss.tools.configitems.builders.PreferencesStorableConfigurationItemBuilderFactory.preferencesWithClass;
import static org.junit.jupiter.api.Assertions.*;

class DefaultStorableConfigurationItemBuilderTest {
    private StorableConfigurationItemBuilder<ConfigTestEnum, String, Integer, String> builder;

    @BeforeEach
    void setUp() {
        builder = preferencesWithClass(DefaultStorableConfigurationItemBuilderTest.class).getBuilder();
    }

    @Test
    void basicTest() {
        StorableConfigurationItem<ConfigTestEnum, String, Integer, String> test = builder
                .setItemID(VALUE_B)
                .setStorageType(String.class)
                .setDefaultValue(1)
                .build();

        assertNotNull(test);

        assertDoesNotThrow(test::save);
        assertDoesNotThrow(test::load);

        assertEquals(1, test.getCurrentValue());
        assertEquals(VALUE_B, test.getKey());
    }
}