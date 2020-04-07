package com.zero.transformer;

import javassist.CtClass;

import java.util.Collections;
import java.util.Set;

public interface TransformHandler {

    default boolean needTransformer(String className) {
        return false;
    }

    default Set<String> includeMethod() {
        return Collections.emptySet();
    }

    default Set<String> excludeMethod() {
        return Collections.emptySet();
    }

    void handle(String className, CtClass cl) throws Exception;

}
