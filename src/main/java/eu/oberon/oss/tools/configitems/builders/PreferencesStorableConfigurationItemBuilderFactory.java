package eu.oberon.oss.tools.configitems.builders;

import eu.oberon.oss.tools.configitems.storage.PreferencesStorageProvider;
import eu.oberon.oss.tools.configitems.storage.StorageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.prefs.Preferences;

/**
 * A factory class for creating instances of {@link StorableConfigurationItemBuilder} with support for persisting preferences using the {@link Preferences}
 * API.
 * <p>
 * This class leverages a {@link PreferencesStorageProvider} to link to the {@link Preferences} storage for storing configuration items. The factory ensures
 * that configuration items are managed with string-based external keys and provides the necessary builder setup for storage capabilities.
 * <p>
 * The following steps are involved when creating a builder instance with this factory:
 * <ol>
 * <li>A {@link PreferencesStorageProvider} is initialized with a {@link Preferences} instance tied to the provided preferences class.</li>
 * <li>The factory provides a builder pre-configured with the string-based external key type and the storage provider for storing and retrieving configuration items.</li>
 * </ol>
 * This class is immutable and thread-safe as its fields are final, and it does not expose any mutable state.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public final class PreferencesStorableConfigurationItemBuilderFactory {

    private final StorageProvider storageProvider;

    private PreferencesStorableConfigurationItemBuilderFactory(Class<?> preferencesClass) {
        Preferences preferences = Preferences.userNodeForPackage(preferencesClass);
        this.storageProvider = new PreferencesStorageProvider(preferences);
    }

    /**
     * Creates a new instance of {@link PreferencesStorableConfigurationItemBuilderFactory} with the specified preferences class.
     *
     * @param preferencesClass The class to which the {@link Preferences} instance is tied.
     *
     * @return A new instance of {@link PreferencesStorableConfigurationItemBuilderFactory}.
     *
     * @since 1.0.0
     */
    public static PreferencesStorableConfigurationItemBuilderFactory preferencesWithClass(@NotNull Class<?> preferencesClass) {
        return new PreferencesStorableConfigurationItemBuilderFactory(preferencesClass);
    }

    /**
     * Provides a builder for creating configuration items with string-based external keys. The builder is pre-configured with a storage provider to facilitate
     * persistence and retrieval of configuration items.
     *
     * @param <I> The identifier type for the configuration item.
     * @param <A> The attribute type associated with the configuration item.
     * @param <P> The property type of the configuration item.
     *
     * @return An instance of {@link StorableConfigurationItemBuilder} pre-configured with string-based external keys and a storage provider.
     *
     * @since 1.0.0
     */
    public <I, A, P> StorableConfigurationItemBuilder<I, String, A, P> getBuilder() {
        return DefaultStorableConfigurationItemBuilder.<I, String, A, P>getInstance()
                .setExtKeyType(String.class)
                .setStorageProvider(storageProvider);
    }
}