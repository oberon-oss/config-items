package eu.oberon.oss.tools.configitems;


import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperProvider;
import eu.oberon.oss.tools.resource.bundle.helper.util.EnumProvider;
import eu.oberon.oss.tools.resource.bundle.helper.util.InitEnumProvider;
import eu.oberon.oss.tools.resource.bundle.helper.util.ResourceBundleUtilityEnum;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.ResourceBundle;

/**
 * The {@code ConfigItems} enum defines a set of configuration-related constants, each representing a specific key or message used within a configuration
 * management system.
 * <p>
 * This enum implements the {@code ResourceBundleUtilityEnum} interface, enabling integration with resource bundles for message localization and customization.
 * <p>
 * Each constant derives a default property name by converting the enumerator name to lowercase and replacing underscores with dots. Custom property names can
 * optionally be assigned through an overloaded constructor.
 * <p>
 * The enum provides a mechanism to retrieve messages or exception objects with parameterized values using an {@code EnumProvider}. Additionally, it allows
 * logging of messages at defined levels.
 * <p>
 * Nested Class: - {@code ConfigItemsResourceBundleHelperProvider}: Provides resource bundle support specific to configuration items, including a key prefix for
 * translations and access to localized messages from the resource bundle.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
@SuppressWarnings({"java:S125", "unused"})
public enum ConfigItems implements ResourceBundleUtilityEnum {
    /**
     * @since 1.0.0
     */
    INTERNAL_KEY_TYPE_SET,
    /**
     * @since 1.0.0
     */
    DEFAULT_VALUE_TYPES_DO_NOT_MATCH,
    /**
     * @since 1.0.0
     */
    TYPE_MUST_NOT_BE_NULL,
    /**
     * @since 1.0.0
     */
    PARAMETER_MUST_NOT_BE_NULL,
    /**
     * @since 1.0.0
     */
    TYPE_MUST_BE_INSTANCE_OF,
    /**
     * @since 1.0.0
     */
    DEFAULT_VALUE_MUST_BE_INSTANCE_OF,
    /**
     * @since 1.0.0
     */
    APPLICATION_DATA_TYPE_SET,
    /**
     * @since 1.0.0
     */
    STORAGE_TYPE_SET_TO,
    /**
     * @since 1.0.0
     */
    STORAGE_TYPE_IS_NOT_SUPPORTED,
    /**
     * @since 1.0.0
     */
    EXTERNAL_KEY_TYPE_NOT_SUPPORTED,
    /**
     * @since 1.0.0
     */
    FUNCTION_MUST_NOT_BE_NULL,
    /**
     * @since 1.0.0
     */
    TYPE_NOT_SUPPORTED,
    /**
     * @since 1.0.0
     */
    CONFIGURATION_ITEM_KEY_MUST_NOT_BE_NULL,
    /**
     * @since 1.0.0
     */
    CONFIGURATION_ALREADY_DEFINED,
    /**
     * @since 1.0.0
     */
    NO_CONFIGURATION_ITEM_REGISTERED,

    ;

    final String propertyName;

    ConfigItems() {
        this.propertyName = name().toLowerCase().replace("_", ".");
    }

    ConfigItems(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getMessage(Object... values) {
        return _provider.getMessage(this, values);
    }

    @Override
    public <T extends Exception> T getException(Class<T> exceptionClass, Object... values) {
        return _provider.getException(this, exceptionClass, values);
    }

    @Override
    public <T extends Exception> T getException(Class<T> exceptionClass, Throwable cause, Object... values) {
        return _provider.getException(this, exceptionClass, cause, values);
    }

    @Override
    public void logMessage(Object... values) {
        _provider.logMessage(this, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <L> void logMessage(L level, Object... values) {
        ((EnumProvider<L, ConfigItems>) _provider).logMessage(this, level, values);
    }

    private static EnumProvider<?, ConfigItems> _provider;

    @SuppressWarnings("unused")
    public static <L> void init(EnumProvider<L, ConfigItems> provider) {
        _provider = provider;
    }

    static {
        InitEnumProvider.init(
                ConfigItems.class,
                "eu.oberon.oss.tools.configitems",
                LoggerFactory.getLogger(ConfigItems.class),
                Level.INFO
        );
    }

    public static class ConfigItemsResourceBundleHelperProvider implements ResourceBundleHelperProvider {
        private final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n-config-items");

        @Override
        public @NotNull ResourceBundle getResourceBundle() {
            return resourceBundle;
        }

        @Override
        public @NotNull String getKeyPrefix() {
            return "eu.oberon.oss.tools.configitems";
        }
    }
}