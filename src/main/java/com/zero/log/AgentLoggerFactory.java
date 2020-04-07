package com.zero.log;

import com.alipay.sofa.common.log.LoggerSpaceManager;
import org.slf4j.Logger;

public class AgentLoggerFactory {
    /** 实际生效的LogSpace */
    private static String currentSpace = null;

    static {
        currentSpace = System.getProperty("agent.log.space", "com.zero");
    }

    public static Logger getLogger(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return getLogger(clazz.getCanonicalName());
    }

    public static Logger getLogger(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        Logger logger = LoggerSpaceManager.getLoggerBySpace(name, currentSpace);
        return logger;
    }

    /**
     * Getter method for property <tt>currentSpace</tt>.
     *
     * @return property value of currentSpace
     */
    public static String getCurrentSpace() {
        return currentSpace;
    }
}
