package com.zero.transformer.impl;

import com.zero.transformer.TransformHandler;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yinchen
 */
public class SlowQueryTransformerHandler implements TransformHandler {

    private Set<String> methods = new HashSet<String>() {
        {
            add("batch");
            add("update");
        }
    };

    @Override
    public void handle(String className, CtClass cl) throws Exception {
        //java.sql.Statement.executeQuery
        //org.apache.ibatis.executor.SimpleExecutor#doQuery
        if (!className.startsWith("org/apache/ibatis/executor/SimpleExecutor")) {
            return;
        }
        System.out.println("------" + className);
        if (!cl.isInterface()) {
            CtMethod[] methods = cl.getDeclaredMethods();
            for (CtMethod method : methods) {
                doMethod(method);
            }
        }
    }

    private void doMethod(CtMethod ctMethod) throws NotFoundException, CannotCompileException {
        String methodName = ctMethod.getName();
        if (methodName.equals("doQuery")) {
            //    ResultSet executeQuery(String sql) throws SQLException;
            System.out.println("------" + methodName);

            ctMethod.addLocalVariable("startTime", CtClass.longType);
            ctMethod.insertBefore("startTime = System.currentTimeMillis();");
            ctMethod.insertAfter("if(System.currentTimeMillis() - startTime > 10) {" +
                    " System.out.println((System.currentTimeMillis() - startTime)+\"ms, slow query,\"+" +
                    "$5.getSql());" +
                    "}");

        }

    }
}
