package com.zero;

import com.zero.classscan.ClassScanner;
import com.zero.transformer.TransformHandler;
import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class Transformer implements ClassFileTransformer {
    private static List<TransformHandler> transformerHandlerList = new ArrayList<>();

    static {
        List<Class<?>> classes = ClassScanner.getClassListByPackage("com.zero.transformer.impl", null);
        for (Class<?> aClass : classes) {
            try {
                TransformHandler handler = (TransformHandler) aClass.newInstance();
                transformerHandlerList.add(handler);
                System.out.println("load..." + handler.getClass());
            } catch (Exception e) {
            }
        }
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.startsWith("java")
                || className.startsWith("javax")
                || className.startsWith("sun")
                || className.startsWith("jdk")
                || className.startsWith("com/sun")
                || className.startsWith("com/intellij")) {
            return null;
        }

        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));

//        pool.insertClassPath(new ClassClassPath(classBeingRedefined));
        CtClass cl = null;
        try {
            cl = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
            for (TransformHandler handler : transformerHandlerList) {
                if (handler.needTransformer(className)) {
                    handler.handle(className, cl);
                }
            }
            byte[] byteArr = cl.toBytecode();
            FileOutputStream fos = new FileOutputStream(new File("/Users/zero/git/javaagenttest/a.class"));
            fos.write(byteArr);
            fos.flush();
            fos.close();
            return cl.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalClassFormatException();
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
    }
}