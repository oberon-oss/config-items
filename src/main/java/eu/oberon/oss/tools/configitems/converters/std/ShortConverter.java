package eu.oberon.oss.tools.configitems.converters.std;

import eu.oberon.oss.tools.configitems.converters.Converter;

import java.util.function.Function;

/**
 * Converter for {@link Short} type.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class ShortConverter implements Converter<Short> {

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    public ShortConverter() {
        // keep Javadoc happy
    }

    @Override
    public Class<Short> getTypeClass() {
        return Short.class;
    }

    @Override
    public Function<Short, String> convertToString() {
        return String::valueOf;
    }

    @Override
    public Function<String, Short> convertFromString() {
        return Short::valueOf;
    }
}
