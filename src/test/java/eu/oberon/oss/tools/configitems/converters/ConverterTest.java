package eu.oberon.oss.tools.configitems.converters;

import eu.oberon.oss.tools.configitems.converters.std.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConverterTest {

    @Test
    void testIntegerConverter() {
        IntegerConverter converter = new IntegerConverter();
        String result = converter.convertToString().apply(123);
        assertEquals("123", result);

        int value = converter.convertFromString().apply(result);
        assertEquals(123, value);
    }

    @Test
    void testLongConverter() {
        LongConverter converter = new LongConverter();
        String result = converter.convertToString().apply(123L);
        assertEquals("123", result);

        long value = converter.convertFromString().apply(result);
        assertEquals(123L, value);
    }

    @Test
    void testBooleanConverter() {
        BooleanConverter converter = new BooleanConverter();
        String result = converter.convertToString().apply(true);
        assertEquals("true", result);

        boolean value = converter.convertFromString().apply(result);
        assertTrue(value);
    }

    @Test
    void testByteConverter() {
        ByteConverter converter = new ByteConverter();
        String result = converter.convertToString().apply((byte) 123);
        assertEquals("123", result);

        byte value = converter.convertFromString().apply(result);
        assertEquals((byte) 123, value);
    }

    @Test
    void testShortConverter() {
        ShortConverter converter = new ShortConverter();
        String result = converter.convertToString().apply((short) 123);
        assertEquals("123", result);

        short value = converter.convertFromString().apply(result);
        assertEquals((short) 123, value);
    }

    @Test
    void testFloatConverter() {
        FloatConverter converter = new FloatConverter();
        String result = converter.convertToString().apply(123.45f);
        assertEquals("123.45", result);

        float value = converter.convertFromString().apply(result);
        assertEquals(123.45f, value);
    }

    @Test
    void testDoubleConverter() {
        DoubleConverter converter = new DoubleConverter();
        String result = converter.convertToString().apply(123.456);
        assertEquals("123.456", result);

        double value = converter.convertFromString().apply(result);
        assertEquals(123.456, value);
    }
}