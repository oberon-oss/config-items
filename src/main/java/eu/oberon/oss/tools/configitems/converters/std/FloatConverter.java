package eu.oberon.oss.tools.configitems.converters.std;

import eu.oberon.oss.tools.configitems.converters.Converter;

import java.util.function.Function;

/**
 * Converter for {@link Float} type.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class FloatConverter implements Converter<Float> {

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    public FloatConverter() {
        // keep Javadoc happy
    }

    @Override
    public Class<Float> getTypeClass() {
        return Float.class;
    }

    @Override
    public Function<Float, String> convertToString() {
        return String::valueOf;
    }

    @Override
    public Function<String, Float> convertFromString() {
        return Float::valueOf;
    }
}
