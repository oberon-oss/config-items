package eu.oberon.oss.tools.configitems;

import java.util.function.Function;

public interface StorableConfigurationItemBuilderAccessor<I, K, A, P> {
    Class<I> getIntKeyType();

    Class<K> getExtKeyType();

    Class<A> getApplicationDataType();

    Class<P> getStorageType() ;

    Function<I, K> getToExtKey();

    Function<K, I> getToIntKey();

    Function<P, A> getToDataType();

    Function<A, P> getToStorageType();

    I getItemID();

    A getDefaultValue();
}
