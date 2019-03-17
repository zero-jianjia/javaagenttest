package com.zero;

import java.lang.instrument.Instrumentation;

/**
 * @author yinchen
 */
public class AgentMain {

    public static void premain(String agentArgs, Instrumentation inst) {
        main(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        main(agentArgs, inst);
    }

    private static synchronized void main(String agentArgs, Instrumentation inst) {
        System.out.println("---------start agent-------------");
        System.out.println(agentArgs);

        /*
         * addTransformer 方法并没有指明要转换哪个类
         * 转换发生在 premain 函数执行之后，main 函数执行之前，
         * 每装载一个类，transform 方法就会执行一次，看看是否需要转换
         */
        inst.addTransformer(new Transformer(), true);

        System.out.println("Agent Main Done");
    }
}
