package eu.oberon.oss.tools.configitems.converters.std;

import eu.oberon.oss.tools.configitems.converters.Converter;

import java.util.function.Function;

/**
 * Converter for {@link Byte} type.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class ByteConverter implements Converter<Byte> {

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    public ByteConverter() {
        // keep Javadoc happy
    }

    @Override
    public Class<Byte> getTypeClass() {
        return Byte.class;
    }

    @Override
    public Function<Byte, String> convertToString() {
        return String::valueOf;
    }

    @Override
    public Function<String, Byte> convertFromString() {
        return Byte::valueOf;
    }
}
