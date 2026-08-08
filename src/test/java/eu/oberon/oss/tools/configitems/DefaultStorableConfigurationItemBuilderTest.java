package eu.oberon.oss.tools.configitems;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultStorableConfigurationItemBuilderTest {

    @Test
    void basicTest() {
        StorableConfigurationItem<ConfigTestEnum, String, Integer, String> test = new DefaultStorableConfigurationItemBuilder<ConfigTestEnum, String, Integer,
                String>()
                .setItemID(ConfigTestEnum.VALUE_B)
                .setDefaultValue(1)
                .build();

        assertNotNull(test);
    }
}