package eu.oberon.oss.tools.configitems.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Provides operation access to the data stored in a {@link StorableConfigurationItem} instance.
 * <p>
 * By exposing the method in this interface, outside users can acces, load, and save their configuration without exposing the entire API.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface ConfigurationItemsRegistryAccessor {
    /**
     * Retrieves the current value of a registered item.
     *
     * @param key The typed key of the item.
     * @param <A> The application-level value type.
     *
     * @return The current value, or {@code null} if no item exists for the key or the current value is {@code null}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws ClassCastException   if the current value is not assignable to the key's value type.
     * @since 1.0.0
     */
    <A> @Nullable A getCurrentValue(ConfigurationItemKey<A> key);

    /**
     * Retrieves the current value of a registered item.
     *
     * @param key The typed key of the item.
     * @param <A> The application-level value type.
     *
     * @return An {@link Optional} containing the current value, or an empty {@link Optional} if no item exists for the key or the current value is
     *         {@code null}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws ClassCastException   if the current value is not assignable to the key's value type.
     * @since 1.0.0
     */
    <A> @NotNull Optional<A> findCurrentValue(ConfigurationItemKey<A> key);

    /**
     * Retrieves the current value of a registered item.
     *
     * @param key The typed key of the item.
     * @param <A> The application-level value type.
     *
     * @return The current value. This may be {@code null} if the registered item currently has a {@code null} value.
     *
     * @throws NullPointerException   if {@code key} is {@code null}.
     * @throws NoSuchElementException if no item exists for the key.
     * @throws ClassCastException     if the current value is not assignable to the key's value type.
     * @since 1.0.0
     */
    <A> @Nullable A getRequiredCurrentValue(ConfigurationItemKey<A> key);

    /**
     * Sets the current value of a registered item.
     * <p>
     * A {@code null} value is allowed and is delegated to the registered item.
     * </p>
     *
     * @param key          The typed key of the item.
     * @param currentValue The new current value.
     * @param <A>          The application-level value type.
     *
     * @return {@code true} if the item was found and updated, otherwise {@code false}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws ClassCastException   if {@code currentValue} is not assignable to the key's value type.
     * @since 1.0.0
     */
    <A> boolean setCurrentValue(ConfigurationItemKey<A> key, @Nullable A currentValue);

    /**
     * Sets the current value of a registered item.
     *
     * @param key          The typed key of the item.
     * @param currentValue The new current value.
     * @param <A>          The application-level value type.
     *
     * @throws NullPointerException   if {@code key} is {@code null}.
     * @throws NoSuchElementException if no item exists for the key.
     * @throws ClassCastException     if {@code currentValue} is not assignable to the key's value type.
     * @since 1.0.0
     */
    <A> void setRequiredCurrentValue(ConfigurationItemKey<A> key, @Nullable A currentValue);

    /**
     * Loads all currently registered items.
     * <p>
     * This method operates on a stable snapshot of the registry. Items registered, removed, or replaced while this method is running are not guaranteed to
     * affect this invocation.
     *
     * @since 1.0.0
     */
    void loadItems();

    /**
     * Loads the specified items.
     * <p>
     * This method operates on a stable snapshot of the selected registry entries. Items registered, removed, or replaced while this method is running are not
     * guaranteed to affect this invocation.
     *
     * @param itemIds The IDs of the items to load.
     *
     * @throws NullPointerException   if {@code itemIds} or one of its elements is {@code null}.
     * @throws NoSuchElementException if no item exists for one of the provided ids.
     * @since 1.0.0
     */
    void loadItems(Object... itemIds);

    /**
     * Loads the specified items.
     * <p>
     * This method operates on a stable snapshot of the selected registry entries. Items registered, removed, or replaced while this method is running are not
     * guaranteed to affect this invocation.
     *
     * @param keys The keys of the items to load.
     *
     * @throws NullPointerException   if {@code keys} or one of its elements is {@code null}.
     * @throws NoSuchElementException if no item exists for one of the provided keys.
     * @since 1.0.0
     */
    void loadItemsByKey(ConfigurationItemKey<?>... keys);

    /**
     * Saves all currently registered items.
     * <p>
     * This method operates on a stable snapshot of the registry. Items registered, removed, or replaced while this method is running are not guaranteed to
     * affect this invocation.
     *
     * @since 1.0.0
     */
    void saveItems();

    /**
     * Saves the specified items.
     * <p>
     * This method operates on a stable snapshot of the selected registry entries. Items registered, removed, or replaced while this method is running are not
     * guaranteed to affect this invocation.
     *
     * @param itemIds The IDs of the items to save.
     *
     * @throws NullPointerException   if {@code itemIds} or one of its elements is {@code null}.
     * @throws NoSuchElementException if no item exists for one of the provided ids.
     * @since 1.0.0
     */
    void saveItems(Object... itemIds);

    /**
     * Saves the specified configuration items identified by their keys.
     * <p>
     * This method operates on a stable snapshot of the registry. Items registered, removed, or replaced while this method is running are not guaranteed to
     * affect this invocation.
     *
     * @param keys The keys identifying the configuration items to save. Each key is expected to correspond to a registered configuration item and its value
     *             type.
     *
     * @throws NullPointerException   if {@code keys} or one of its elements is {@code null}.
     * @throws NoSuchElementException if no item exists for one of the provided keys.
     */
    void saveItemsByKey(ConfigurationItemKey<?>... keys);
}
