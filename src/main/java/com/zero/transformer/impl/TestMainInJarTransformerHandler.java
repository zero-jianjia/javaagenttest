package com.zero.transformer.impl;

import com.zero.Utils.AssistUtil;
import com.zero.transformer.SimpleCostTransformerHandler;

public class TestMainInJarTransformerHandler extends SimpleCostTransformerHandler {
    private static final String CLASS_NAME = "com.zero.test.TestMainInJar";

    @Override
    public boolean needTransformer(String className) {
        String originClassName = AssistUtil.replaceClassName(className);
        return CLASS_NAME.equals(originClassName);
    }
}
