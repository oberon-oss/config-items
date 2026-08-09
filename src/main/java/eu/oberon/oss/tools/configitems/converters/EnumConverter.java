package eu.oberon.oss.tools.configitems.converters;

import java.util.function.Function;

/**
 * A generic converter implementation for converting between {@link Enum} types and their {@link String} representations.
 * <p>
 * This class provides functionality to: - Convert an enum value to its string representation (name of the enum constant). - Convert a string back to the
 * corresponding enum value.
 *
 * @param <E> The type of the enum class that can be converted to and from strings.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class EnumConverter<E extends Enum<E>> implements Converter<E> {

    private final Class<E> enumClass;

    /**
     * Constructs an EnumConverter for the specified enum class.
     *
     * @param enumClass The enum class to be converted.
     *
     * @since 1.0.0
     */
    public EnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Class<E> getTypeClass() {
        return enumClass;
    }

    @Override
    public Function<E, String> convertToString() {
        return Enum::name;
    }

    @Override
    public Function<String, E> convertFromString() {
        return value -> Enum.valueOf(enumClass, value);
    }
}
