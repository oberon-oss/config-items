package eu.oberon.oss.tools.configitems.items;

import eu.oberon.oss.tools.configitems.storage.StorageProvider;

import java.util.function.Function;

/**
 * A default implementation of {@link ConfigurationItemAccessor}.
 *
 * @param intKeyType          the internal key type
 * @param extKeyType          the external key type
 * @param applicationDataType the data type as used by an application
 * @param storageType         the type of class the 'applicationDataType' is stored by the {@link StorageProvider}
 * @param toExtKey            function to convert an internal key representation {@code <I>} to its external {@code <K>} form
 * @param toDataType          function to convert a stored value of type {@code <P>} to the application data type {@code <A>}
 * @param toStorageType       function to convert a value of type {@code <A>} to the storage type {@code <P>}
 * @param <I>                 the internal key type
 * @param <K>                 the external key type
 * @param <A>                 the data type as used by an application
 * @param <P>                 the type of class the 'applicationDataType' is stored by the {@link StorageProvider}
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record DefaultConfigurationItemAccessor<I, K, A, P>(Class<I> intKeyType, Class<K> extKeyType, Class<A> applicationDataType, Class<P> storageType,
                                                           Function<I, K> toExtKey, Function<P, A> toDataType,
                                                           Function<A, P> toStorageType) implements ConfigurationItemAccessor<I, K, A, P> {
}