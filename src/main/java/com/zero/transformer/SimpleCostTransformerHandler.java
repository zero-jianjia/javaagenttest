package com.zero.transformer;

import com.zero.Utils.AssistUtil;
import com.zero.Utils.Constant;
import javassist.*;

import java.util.LinkedHashMap;


public abstract class SimpleCostTransformerHandler implements TransformHandler {
    @Override
    public void handle(String className, CtClass ctClass) throws Exception {
        if (!ctClass.isInterface()) {
            CtField param = new CtField(
                    ClassPool.getDefault().get("org.slf4j.Logger"),
                    Constant.LOGGER_FILED_NAME,
                    ctClass);
            param.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);

            ctClass.addField(param, CtField.Initializer.byCall(
                    ClassPool.getDefault().get(Constant.LOGGER_FACTORY_CLASS),
                    Constant.LOGGER_FACTORY_METHOD));
            CtMethod[] methods = ctClass.getDeclaredMethods();
            for (CtMethod method : methods) {
                doMethod(method);
            }
        }
    }

    private void doMethod(CtMethod ctMethod) throws NotFoundException, CannotCompileException {
        String methodName = ctMethod.getName();
        if (excludeMethod().contains(methodName)) {
            return;
        }

        if (!includeMethod().isEmpty() && !includeMethod().contains(methodName)) {
            return;
        }

        ctMethod.addLocalVariable("startTimeAgent", CtClass.longType);
        ctMethod.insertBefore("startTimeAgent = System.currentTimeMillis();");

        String systemPrintStr = null;
        LinkedHashMap<String, String> parmAndValue = AssistUtil.getParmAndValue(ctMethod);
        if (parmAndValue != null) {
            systemPrintStr = AssistUtil.parmSystemPrint(parmAndValue);
        }

        if (systemPrintStr != null) {
            ctMethod.insertAfter("System.out.println(\"cost:\" + (System.currentTimeMillis() - startTimeAgent) + \"ms, \" + " + systemPrintStr + ");");
        }
        else {
            ctMethod.insertAfter("System.out.println(\"cost:\" + (System.currentTimeMillis() - startTimeAgent) + \"ms\");");
        }
    }
}
