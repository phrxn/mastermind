package com.quazzom.mastermind.unit.utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.utils.SecretDecoder;

class SecretDecoderTest {

    private final SecretDecoder decoder = new SecretDecoder();

    @Test
    void shouldEncodeListOfIntegersIntoCommaSeparatedString() {
        List<Integer> input = List.of(1, 2, 3);

        String result = decoder.encode(input);

        assertEquals("1,2,3", result);
    }

    @Test
    void shouldReturnEmptyStringWhenEncodingEmptyList() {
        List<Integer> input = List.of();

        String result = decoder.encode(input);

        assertEquals("", result);
    }

    @Test
    void shouldDecodeCommaSeparatedStringIntoListOfIntegers() {
        String input = "1,2,3";

        List<Integer> result = decoder.decode(input);

        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void shouldTrimSpacesWhenDecodingValues() {
        String input = "1, 2, 3";

        List<Integer> result = decoder.decode(input);

        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void shouldIgnoreBlankValuesWhenDecoding() {
        String input = "1,,2, ,3";

        List<Integer> result = decoder.decode(input);

        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void shouldReturnEmptyListWhenInputIsNull() {
        List<Integer> result = decoder.decode(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenInputIsBlank() {
        List<Integer> result = decoder.decode("   ");

        assertTrue(result.isEmpty());
    }
}
