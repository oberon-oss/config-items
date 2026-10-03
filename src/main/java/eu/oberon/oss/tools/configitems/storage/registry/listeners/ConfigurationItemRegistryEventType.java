package eu.oberon.oss.tools.configitems.storage.registry.listeners;

/**
 * Types of events that can occur in a configuration item registry.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public enum ConfigurationItemRegistryEventType {
    
    /**
     * A configuration item has been registered.
     *
     * @since 1.0.0
     */
    REGISTERED,
    /**
     * An existing configuration item has been replaced.
     *
     * @since 1.0.0
     */
    REPLACED,
    /**
     * A configuration item has been unregistered.
     *
     * @since 1.0.0
     */
    UNREGISTERED,
    /**
     * All configuration items have been cleared from the registry.
     *
     * @since 1.0.0
     */
    CLEARED,
    /**
     * The value of a configuration item has changed.
     *
     * @since 1.0.0
     */
    VALUE_CHANGED,
    /**
     * Configuration items have been loaded into the registry.
     *
     * @since 1.0.0
     */
    LOADED,
    /**
     * Configuration items have been saved.
     *
     * @since 1.0.0
     */
    SAVED
}
