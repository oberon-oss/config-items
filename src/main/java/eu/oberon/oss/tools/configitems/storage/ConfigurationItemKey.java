package eu.oberon.oss.tools.configitems.storage;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Typed key used to identify a configuration item and the type of value it stores.
 * <p>
 * The {@code id} is the actual key used for registry lookup. The {@code valueType} is used to provide type-safe access to the item's current value.
 * </p>
 *
 * @param id        The registry identifier of the configuration item.
 * @param valueType The expected application-level value type of the configuration item.
 * @param <A>       The application-level value type.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record ConfigurationItemKey<A>(@NotNull Object id, @NotNull Class<A> valueType) {

    /**
     * Creates a typed configuration item key.
     *
     * @param id        The registry identifier of the configuration item.
     * @param valueType The expected application-level value type of the configuration item.
     * @param <A>       The application-level value type.
     *
     * @return A new typed configuration item key.
     *
     * @throws NullPointerException if {@code id} or {@code valueType} is {@code null}.
     * @since 1.0.0
     */
    public static <A> @NotNull ConfigurationItemKey<A> of(@NotNull Object id, @NotNull Class<A> valueType) {
        return new ConfigurationItemKey<>(id, valueType);
    }

    /**
     * Creates a typed configuration item key using the type of the supplied default value.
     *
     * @param id           The registry identifier of the configuration item.
     * @param defaultValue The default value used to infer the application-level value type.
     * @param <A>          The application-level value type.
     *
     * @return A new typed configuration item key.
     *
     * @throws NullPointerException if {@code id} or {@code defaultValue} is {@code null}.
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <A> @NotNull ConfigurationItemKey<A> ofDefaultValue(@NotNull Object id, @NotNull A defaultValue) {
        Objects.requireNonNull(defaultValue, "Parameter: defaultValue");
        return new ConfigurationItemKey<>(id, (Class<A>) defaultValue.getClass());
    }

    /**
     * Creates a new instance.
     *
     * @throws NullPointerException if {@code id} or {@code valueType} is {@code null}.
     * @since 1.0.0
     */
    public ConfigurationItemKey {
        Objects.requireNonNull(id, "Parameter: id");
        Objects.requireNonNull(valueType, "Parameter: valueType");
    }
}