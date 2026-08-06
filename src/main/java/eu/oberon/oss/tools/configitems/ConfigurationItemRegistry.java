package eu.oberon.oss.tools.configitems;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

/**
 * Manages a collection of {@link ConfigurationItem} instances, providing functionality to register, retrieve, set, load, and store configuration items.
 * <p>
 * The registry is backed by a {@link Preferences} storage system and supports thread-safe operations.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class ConfigurationItemRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationItemRegistry.class);

    private final Preferences preferences;
    private final Map<String, ConfigurationItem<?>> items = new ConcurrentHashMap<>();


    /**
     * Initializes a new instance of the {@code ConfigurationItemRegistry} class with the specified {@code Preferences} storage system. This constructor ensures
     * the registry has a backing {@code Preferences} instance for managing configuration persistence.
     *
     * @param preferences the {@code Preferences} instance used for storing and retrieving configuration data. Must not be null.
     *
     * @since 1.0.0
     */
    public ConfigurationItemRegistry(@NotNull Preferences preferences) {
        this.preferences = preferences;
    }


    /**
     * Registers a {@link ConfigurationItem} into the registry.
     *
     * @param <A>  the type of the value held by the {@link ConfigurationItem}.
     * @param item the configuration item to register. Must not be null and must have a unique name within the registry.
     *
     * @throws IllegalArgumentException if an item with the same name already exists.
     * @since 1.0.0
     */
    public <A> void register(@NotNull ConfigurationItem<A> item) {
        register(item, false);
    }

    /**
     * Registers a {@link ConfigurationItem} instance into the registry. Allows optionally replacing an existing item with the same name.
     *
     * @param <A>             the type of the value held by the {@link ConfigurationItem}.
     * @param item            the configuration item to register. Must not be null and must have a unique name within the registry unless replacement is
     *                        allowed.
     * @param replaceIfExists whether to replace the existing item if an item with the same name already exists. If false and an item with the same name exists,
     *                        an {@link IllegalArgumentException} will be thrown.
     *
     * @throws IllegalArgumentException if an item with the same name already exists and {@code replaceIfExists} is {@code false}.
     */
    public <A> void register(@NotNull ConfigurationItem<A> item, boolean replaceIfExists) {
        if (items.containsKey(item.getItemName()) && !replaceIfExists) {
            throw new IllegalArgumentException("Item with name '" + item.getItemName() + "' already exists in the registry.");
        }
        items.put(item.getItemName(), item);
    }

    /**
     * Retrieves the value of a configuration item by its name.
     *
     * @param name the name of the configuration item.
     * @param <A>  the type of the value held by the {@link ConfigurationItem}.
     *
     * @return the value of the configuration item, or {@code null} if the item does not exist.
     *
     * @since 1.0.0
     */
    public <A> @Nullable A getItemValue(@NotNull String name) {
        ConfigurationItem<A> item = getItem(name);
        return item == null ? null : item.getConfigItemValue();
    }

    /**
     * Sets the value of a configuration item by its name.
     *
     * @param name  the name of the configuration item.
     * @param value the new value to set.
     * @param <A>   the type of the value held by the {@link ConfigurationItem}.
     *
     * @throws IllegalArgumentException if the item does not exist.
     * @since 1.0.0
     */
    public <A> void setItemValue(@NotNull String name, @NotNull A value) {
        ConfigurationItem<A> item = getItem(name);
        if (item != null) {
            item.setConfigItemValue(value);
        }
    }

    /**
     * Loads the state of all registered {@link ConfigurationItem} instances from the underlying {@code Preferences} storage.
     *
     * @since 1.0.0
     */
    public void loadData() {
        for (ConfigurationItem<?> item : items.values()) {
            item.load(preferences);
            LOGGER.info("Loaded item: {}", item.getItemName());
        }
    }

    /**
     * Stores the state of all registered {@link ConfigurationItem} instances to the underlying {@code Preferences} storage.
     *
     * @since 1.0.0
     */
    public void storeData() {
        for (ConfigurationItem<?> item : items.values()) {
            item.store(preferences);
            LOGGER.info("Stored item: {}", item.getItemName());
        }
    }

    private <A> ConfigurationItem<A> getItem(String name) {
        //noinspection unchecked
        return (ConfigurationItem<A>) items.get(name);
    }
}
