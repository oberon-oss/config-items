package eu.oberon.oss.tools.configitems;

import java.util.function.Function;

public interface StorableConfigurationItemBuilderAccessor<I, K, A, P> {
    public Class<I> getIntKeyType();

    public Class<K> getExtKeyType();

    public Class<A> getApplicationDataType();

    public Class<P> getStorageType() ;

    public Function<I, K> getToExtKey();

    public Function<K, I> getToIntKey();

    public Function<P, A> getToDataType();

    public Function<A, P> getToStorageType();

    public I getItemID();

    public A getDefaultValue();
}
