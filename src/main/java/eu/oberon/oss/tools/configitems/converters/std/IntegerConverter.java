package eu.oberon.oss.tools.configitems.converters.std;

import eu.oberon.oss.tools.configitems.converters.Converter;

import java.util.function.Function;

/**
 * Converter for {@link Integer} type.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class IntegerConverter implements Converter<Integer> {

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    public IntegerConverter() {
        // keep Javadoc happy
    }

    @Override
    public Class<Integer> getTypeClass() {
        return Integer.class;
    }

    @Override
    public Function<Integer, String> convertToString() {
        return Object::toString;
    }

    @Override
    public Function<String, Integer> convertFromString() {
        return Integer::parseInt;
    }
}
