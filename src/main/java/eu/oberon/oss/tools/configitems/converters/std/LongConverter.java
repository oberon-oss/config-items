package eu.oberon.oss.tools.configitems.converters.std;

import eu.oberon.oss.tools.configitems.converters.Converter;

import java.util.function.Function;

/**
 * Converter for {@link Long} type.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class LongConverter implements Converter<Long> {

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    public LongConverter() {
        // keep Javadoc happy
    }

    @Override
    public Class<Long> getTypeClass() {
        return Long.class;
    }

    @Override
    public Function<Long, String> convertToString() {
        return String::valueOf;
    }

    @Override
    public Function<String, Long> convertFromString() {
        return Long::valueOf;
    }
}
