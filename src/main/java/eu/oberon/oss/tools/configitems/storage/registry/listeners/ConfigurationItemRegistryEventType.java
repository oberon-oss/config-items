package eu.oberon.oss.tools.configitems.storage.registry.listeners;

/**
 * Types of events that can occur in a configuration item registry.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public enum ConfigurationItemRegistryEventType {
    /**
     * @since 1.0.0
     */
    REGISTERED,
    /**
     * @since 1.0.0
     */
    REPLACED,
    /**
     * @since 1.0.0
     */
    UNREGISTERED,
    /**
     * @since 1.0.0
     */
    CLEARED,
    /**
     * @since 1.0.0
     */
    VALUE_CHANGED,
    /**
     * @since 1.0.0
     */
    LOADED,
    /**
     * @since 1.0.0
     */
    SAVED
}
