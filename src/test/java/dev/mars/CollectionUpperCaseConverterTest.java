package dev.mars;


import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CollectionUpperCaseConverterTest {

    private final CollectionUpperCaseConverter converter = new CollectionUpperCaseConverter();

    @Test
    public void testConvertToUpperCase() {
        List<String> input = Arrays.asList("hello", "world");
        List<String> expected = Arrays.asList("HELLO", "WORLD");
        List<String> result = converter.apply(input);
        assertEquals(expected, result);
    }

    @Test
    public void testEmptyList() {
        List<String> input = Collections.emptyList();
        List<String> expected = Collections.emptyList();
        List<String> result = converter.apply(input);
        assertEquals(expected, result);
    }

    @Test
    public void testMixedCase() {
        List<String> input = Arrays.asList("HeLLo", "WorLD");
        List<String> expected = Arrays.asList("HELLO", "WORLD");
        List<String> result = converter.apply(input);
        assertEquals(expected, result);
    }

    @Test
    public void testSingleElement() {
        List<String> input = Collections.singletonList("java");
        List<String> expected = Collections.singletonList("JAVA");
        List<String> result = converter.apply(input);
        assertEquals(expected, result);
    }

    @Test
    public void testNullElement() {
        List<String> input = Arrays.asList("hello", null, "world");
        assertThrows(NullPointerException.class, () -> {
            converter.apply(input);
        });
    }
}