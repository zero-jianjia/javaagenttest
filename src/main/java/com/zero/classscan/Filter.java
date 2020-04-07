package com.zero.classscan;

@FunctionalInterface
public interface Filter {
    boolean filter(Class<?> cls);
}
