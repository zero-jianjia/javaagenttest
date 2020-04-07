package com.zero.Utils;

import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AssistUtil {

    public static String replaceClassName(String className) {
        return className.replace("/", ".");
    }

    public static LinkedHashMap<String, String> getParmAndValue(CtMethod ctMethod) throws NotFoundException {
        int parmLength = ctMethod.getParameterTypes().length;
        if (parmLength == 0) {
            return null;
        }

        boolean isStatic = Modifier.isStatic(ctMethod.getModifiers());

        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            throw new NotFoundException("LocalVariableAttribute not found in " + ctMethod.getName());
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

    public static String parmSystemPrint(LinkedHashMap<String, String> parmAndValue) {
        List<String> formatStringlines = new ArrayList<>();
        parmAndValue.forEach((parm, value) ->
                        formatStringlines.add("\"" + parm + ": \" + " + value)
                            );
        StringBuilder stringBuilder = new StringBuilder(32);
        stringBuilder.append(String.join(" + \", \" + ", formatStringlines));
        return stringBuilder.toString();
    }
}
