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
public final class StorableConfigurationItemBuilderFactory {

    @NotNull
    private final StorageProvider storageProvider;
    @NotNull
    private final Object externalKeyType;
    private final boolean autoGenerate;

    private <K> StorableConfigurationItemBuilderFactory(@NotNull StorageProvider storageProvider, K externalKeyType, boolean autoGenerate) {
        this.storageProvider = storageProvider;
        this.externalKeyType = externalKeyType;
        this.autoGenerate = autoGenerate;
    }

    /**
     * Creates a new instance of {@link StorableConfigurationItemBuilderFactory}.
     *
     * @param storageProvider The storage provider for storing and retrieving configuration items.
     * @param externalKeyType The external key type used for identifying configuration items.
     * @param autoGenerate    A flag indicating whether to enable auto-generation of configuration items.
     * @param <K>             The type of the external key.
     *
     * @return A new instance of {@link StorableConfigurationItemBuilderFactory}.
     *
     * @since 1.0.0
     */
    public static <K> StorableConfigurationItemBuilderFactory create(@NotNull StorageProvider storageProvider, Class<K> externalKeyType, boolean autoGenerate) {
        return new StorableConfigurationItemBuilderFactory(storageProvider, externalKeyType, autoGenerate);
    }

    /**
     * Creates a new instance of {@link StorableConfigurationItemBuilderFactory} with auto-generation enabled.
     *
     * @param storageProvider The storage provider for storing and retrieving configuration items.
     * @param externalKeyType The external key type used for identifying configuration items.
     * @param <K>             The type of the external key.
     *
     * @return A new instance of {@link StorableConfigurationItemBuilderFactory}.
     *
     * @since 1.0.0
     */
    public static <K> StorableConfigurationItemBuilderFactory create(@NotNull StorageProvider storageProvider, Class<K> externalKeyType) {
        return new StorableConfigurationItemBuilderFactory(storageProvider, externalKeyType, true);
    }

    /**
     * Creates a new instance of {@link StorableConfigurationItemBuilderFactory} with auto-generation enabled.
     *
     * @param storageProvider The storage provider for storing and retrieving configuration items.
     * @param autoGenerate    Whether to enable auto-generation of configuration items.
     *
     * @return A new instance of {@link StorableConfigurationItemBuilderFactory}.
     *
     * @since 1.0.0
     */
    public static StorableConfigurationItemBuilderFactory create(@NotNull StorageProvider storageProvider, boolean autoGenerate) {
        return new StorableConfigurationItemBuilderFactory(storageProvider, String.class, autoGenerate);
    }

    /**
     * Creates a new instance of {@link StorableConfigurationItemBuilderFactory} with the specified preferences class.
     *
     * @param storageProvider The storage provider for storing and retrieving configuration items.
     *
     * @return A new instance of {@link StorableConfigurationItemBuilderFactory}.
     *
     * @since 1.0.0
     */
    public static StorableConfigurationItemBuilderFactory create(@NotNull StorageProvider storageProvider) {
        return new StorableConfigurationItemBuilderFactory(storageProvider, String.class, true);
    }

    /**
     * Provides a builder for creating instances of {@link StorableConfigurationItemBuilder}. This builder is pre-configured with a storage provider, allowing
     * for seamless integration of persistence and retrieval functionalities.
     *
     * @param <I> The internal key type used to uniquely identify the configuration item within the system.
     * @param <K> The external key type used for external representation of the configuration item.
     * @param <A> The application data type representing the value used within the application.
     * @param <P> The storage type for persistence of the configuration item's value.
     *
     * @return An instance of {@link StorableConfigurationItemBuilder} pre-configured with the specified storage provider.
     *
     * @since 1.0.0
     */
    public <I, K, A, P> StorableConfigurationItemBuilder<I, K, A, P> getInstance() {
        //noinspection unchecked
        return new DefaultStorableConfigurationItemBuilder<I, K, A, P>()
                .setStorageProvider(storageProvider)
                .setExtKeyType((Class<K>) externalKeyType)
                .setAutoGeneration(autoGenerate);
    }
}