package eu.oberon.oss.tools.configitems.converters.std;

import eu.oberon.oss.tools.configitems.converters.Converter;

import java.util.function.Function;

/**
 * Converter for {@link Double} type.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class DoubleConverter implements Converter<Double> {
    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    public DoubleConverter() {
        // keep Javadoc happy
    }

    @Override
    public Class<Double> getTypeClass() {
        return Double.class;
    }

    @Override
    public Function<Double, String> convertToString() {
        return String::valueOf;
    }

    @Override
    public Function<String, Double> convertFromString() {
        return Double::valueOf;
    }
}
