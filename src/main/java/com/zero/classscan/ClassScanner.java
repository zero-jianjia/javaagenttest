package com.zero.classscan;

import java.util.List;

public class ClassScanner {

    /**
     * 获取指定包名中相关类
     */
    public static List<Class<?>> getClassListByPackage(String packageName,
            Filter filter) {
        ClassTemplate classTemplate = new ClassTemplate(packageName, filter);
        return classTemplate.getClassList();
    }
}
