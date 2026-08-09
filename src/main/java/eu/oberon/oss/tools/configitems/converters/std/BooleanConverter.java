package eu.oberon.oss.tools.configitems.converters.std;

import eu.oberon.oss.tools.configitems.converters.Converter;

import java.util.function.Function;

/**
 * Converter for {@code Boolean} values.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class BooleanConverter implements Converter<Boolean> {

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    public BooleanConverter() {
        // keep Javadoc happy
    }

    @Override
    public Class<Boolean> getTypeClass() {
        return Boolean.class;
    }

    @Override
    public Function<Boolean, String> convertToString() {
        return String::valueOf;
    }

    @Override
    public Function<String, Boolean> convertFromString() {
        return Boolean::valueOf;
    }
}
