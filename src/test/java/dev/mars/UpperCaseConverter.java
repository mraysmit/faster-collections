package dev.mars;

public class UpperCaseConverter implements ElementTransformer<String, String> {
    @Override
    public String apply(String element) {
        return element.toUpperCase();
    }
}
