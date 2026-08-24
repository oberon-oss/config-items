package eu.oberon.oss.tools.configitems.storage.registry.listeners;

/**
 * Listener for events emitted by a configuration item registry.
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface ConfigurationItemRegistryListener {

    /**
     * Handles a registry event.
     *
     * @param event The registry event.
     *
     * @since 1.0.0
     */
    void onRegistryEvent(ConfigurationItemRegistryEvent event);
}