package eu.oberon.oss.tools.configitems.storage.registry.listeners;

import eu.oberon.oss.tools.configitems.storage.RegisteredConfigurationItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event describing a change or operation that occurred in a configuration item registry.
 *
 * @param type         The event type.
 * @param key          The affected item key, if available.
 * @param item         The affected item, if available.
 * @param previousItem The previous item, if this event replaced an item.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record ConfigurationItemRegistryEvent(
        @NotNull ConfigurationItemRegistryEventType type,
        @Nullable Object key,
        @Nullable RegisteredConfigurationItem item,
        @Nullable RegisteredConfigurationItem previousItem
) {
}