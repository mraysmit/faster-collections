package dev.mars;

import java.util.List;
import java.util.stream.Collectors;

public class CollectionUpperCaseConverter implements ElementTransformer<List<String>, List<String>> {
    @Override
    public List<String> apply(List<String> param) {
        return param.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }
}
