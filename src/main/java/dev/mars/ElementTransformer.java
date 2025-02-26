package dev.mars;

@FunctionalInterface
public interface ElementTransformer<R, S> {
    S apply(R param);
}
