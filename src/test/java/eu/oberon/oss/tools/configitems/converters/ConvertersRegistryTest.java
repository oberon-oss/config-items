package eu.oberon.oss.tools.configitems.converters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConvertersRegistryTest {


    public static Stream<Arguments> testConvertersRegistry() {

        return Stream.of(
                Arguments.of(Integer.class),
                Arguments.of(Long.class),
                Arguments.of(Byte.class),
                Arguments.of(Short.class),
                Arguments.of(Boolean.class),
                Arguments.of(Float.class),
                Arguments.of(Double.class)
        );
    }

    @ParameterizedTest
    @MethodSource()
    void testConvertersRegistry(Class<?> classType) {
        ConvertersRegistry registry = new ConvertersRegistry();
        assertNotNull(registry);

        Converter<?> converter = assertDoesNotThrow(() -> registry.getConverterForClassType(classType));
        assertNotNull(converter);
    }
}