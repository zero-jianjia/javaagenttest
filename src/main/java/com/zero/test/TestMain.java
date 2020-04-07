package com.zero.test;

import com.zero.Utils.AssistUtil;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author yinchen
 */
public class TestMain {
    public static void main(String[] args) {

        System.out.println((double)0.03/0);

        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        CtClass cl = null;
        try {
            cl = pool.get("com.zero.test.TestMainInJar");
            CtMethod[] methods = cl.getDeclaredMethods();

            for (CtMethod method : methods) {
                doMethod(method);
            }
//            byte[] byteArr = cl.toBytecode();
//            FileOutputStream fos = new FileOutputStream(new File("/Users/zero/git/javaagenttest/src/main/java/com/zero/test/a.class"));

            cl.writeFile("/Users/zero/git/javaagenttest/src/main/java/");
//            fos.write(byteArr);
//            fos.flush();
//            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
    }

    private static void doMethod(CtMethod ctMethod) throws NotFoundException, CannotCompileException {
        String methodName = ctMethod.getName();

        if(methodName.equals("main")){
            return;
        }
//        boolean isStatic = Modifier.isStatic(ctMethod.getModifiers());
//
//        System.out.println(methodName + " isStatic: " + isStatic);
//        System.out.println(methodName + " getParameterTypes: ");
//        for (CtClass parameterType : ctMethod.getParameterTypes()) {
//        }

        // 使用javaassist的反射方法获取方法的参数名
//        MethodInfo methodInfo = ctMethod.getMethodInfo();
//        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
//        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
//        if (attr == null) {
//            throw new NotFoundException("LocalVariableAttribute not found in " + methodName);
//        }

//        String[] paramNames = new String[ctMethod.getParameterTypes().length];
//        for (int i = 0; i < paramNames.length; i++) {
//            if (isStatic) {
//                paramNames[i] = attr.variableName(i);
//            }
//            else {
//                paramNames[i] = attr.variableName(i + 1);
//            }
//        }

//        int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
//        for (int i = 0; i < ctMethod.getParameterTypes().length; i++) {
//            System.out.print(attr.variableName(i + pos));
//            if (i < ctMethod.getParameterTypes().length - 1) {
//                System.out.print(",");
//            }
//        }
//
//        LinkedHashMap<String, String> parm = getParmAndValue(isStatic, attr, ctMethod.getParameterTypes().length);
//        if (parm != null) {
//            String a = parmSystemPrint(parm);
//            ctMethod.insertBefore(a);
//        }

//        if(!isStatic){
////            ctMethod.insertBefore("System.out.println($1);");
//        }else {
//            ctMethod.insertBefore("System.out.println($0);");
//        }

//        if ("test".equalsIgnoreCase(method.getName())) {
//            //添加局部变量，如果不同过addLocalVariable设置，在调用属性时将出现compile error: no such field: startTime
//        ctMethod.insertAfter("System.out.println(\"" + ctMethod.getName() + "\"");

//        ctMethod.insertBefore("System.out.println(\"----\"+ $1);");
//        ctMethod.insertBefore("LOGGER_.info(\"ttttttt\");");


//        ctMethod.addLocalVariable("startTime", CtClass.longType);
//        ctMethod.insertBefore("System.out.println(startTime);");
//        ctMethod.insertBefore("startTime = System.currentTimeMillis();");
//////			method.insertBefore("long startTime = System.currentTimeMillis();System.out.println(startTime);");
//        ctMethod.insertBefore("System.out.println(\"insert before ......\");");
//
//        ctMethod.insertBefore("System.out.println($args);");



//        ctMethod.insertAfter("System.out.println(\"leave " + ctMethod.getName() + " and time is :\" + (System.currentTimeMillis() - startTime));");
//        ctMethod.insertAfter("if((System.currentTimeMillis() - startTime) > 1) { " +
//                "System.out.println((System.currentTimeMillis() - startTime) + \"*****\");" +
//                "" +
//                " }");
//        }


        ctMethod.addLocalVariable("startTimeAgent", CtClass.longType);
        ctMethod.insertBefore("startTimeAgent = System.currentTimeMillis();");

        String systemPrintStr = null;
        LinkedHashMap<String, String> parmAndValue = AssistUtil.getParmAndValue(ctMethod);
        if (parmAndValue != null) {
            systemPrintStr = AssistUtil.parmSystemPrint(parmAndValue);
        }

        System.out.println(systemPrintStr);
        if (systemPrintStr != null) {
            ctMethod.insertAfter("System.out.println(\"cost:\" + (System.currentTimeMillis() - startTimeAgent) + \"ms, \" + " + systemPrintStr + ");");
        }
        else {
            ctMethod.insertAfter("System.out.println(\"cost:\" + (System.currentTimeMillis() - startTimeAgent) + \"ms\");");
        }

    }


    private static LinkedHashMap<String, String> getParmAndValue(boolean isStatic,
            LocalVariableAttribute attr, int parmLength) {
        if (parmLength == 0) {
            return null;
        }
        LinkedHashMap<String, String> parmAndValue = new LinkedHashMap<>();
        String[] paramNames = new String[parmLength];
        for (int i = 0; i < paramNames.length; i++) {
            if (isStatic) {
                // 静态方法第一个 $0 是 null
                parmAndValue.put(attr.variableName(i), "$" + (i + 1));
            }
            else {
                // 非静态方法第一个 $0 是 this
                parmAndValue.put(attr.variableName(i + 1), "$" + (i + 1));
            }
        }
        return parmAndValue;
    }

    private static String parmSystemPrint(LinkedHashMap<String, String> parmAndValue) {
        List<String> formatStringlines = new ArrayList<>();
        parmAndValue.forEach((parm, value) ->
                        formatStringlines.add("\"" + parm + ": \" + " + value)
                            );
        StringBuilder stringBuilder = new StringBuilder(32);
        stringBuilder.append("System.out.println");
        stringBuilder.append("(");
        stringBuilder.append(String.join(" + \", \" + ", formatStringlines));
        stringBuilder.append(");");
        return stringBuilder.toString();
    }
}
