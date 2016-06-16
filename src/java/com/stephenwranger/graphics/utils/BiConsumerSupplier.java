package com.stephenwranger.graphics.utils;

@FunctionalInterface
public interface BiConsumerSupplier<S,T,U> {
   U getValue(final S value1, final T value2);
}
