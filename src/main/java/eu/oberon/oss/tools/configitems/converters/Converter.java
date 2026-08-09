package eu.oberon.oss.tools.configitems.converters;

import java.util.function.Function;

/**
 * Provides conversion between a string and a user defined type.
 *
 * @param <D> the type of class that can be converted to {@link String} and this type and vice versa
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface Converter<D> {

    /**
     * Returns the class type of the converter that can be converted to {@link String} and this type and vice versa.
     *
     * @return the class type of the converter
     *
     * @since 1.0.0
     */

    Class<D> getTypeClass();

    /**
     * Returns a function that converts the given object type {@code <D>}to a string.
     *
     * @return the function that converts the given object type {@code <D>}to a string
     *
     * @since 1.0.0
     */
    Function<D, String> convertToString();

    /**
     * Returns a function that converts the given string to the object type {@code <D>}.
     *
     * @return the function that converts the given string to the object type {@code <D>}
     *
     * @since 1.0.0
     */
    Function<String, D> convertFromString();
}
