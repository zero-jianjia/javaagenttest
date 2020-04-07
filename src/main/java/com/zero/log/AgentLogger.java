package com.zero.log;

import org.slf4j.Logger;

import java.io.File;

public class AgentLogger {
    private static Logger log;
    private static Logger monitorLog;

    private static volatile boolean inited;

    private static final String LOG_PATH = "logging.path";
    private static final String LOG_PATH_DEFAULT = System.getProperty("user.home") + File.separator
            + "logs";
    private static final String CLIENT_LOG_LEVEL = "agent.log.level";
    private static final String CLIENT_LOG_LEVEL_DEFAULT = "INFO";
    private static final String CLIENT_LOG_ENCODE = "agent.log.encode";
    private static final String CLIENT_LOG_ENCODE_DEFAULT = "GBK";

    static {
        init();
        log = createLogger("agent");
        monitorLog = createLogger("monitor");
    }

    public static void init() {
        if (!inited) {
            inited = true;
            initProperties();
        }
    }

    private static void initProperties() {
        String logPath = System.getProperty(LOG_PATH, LOG_PATH_DEFAULT);
        String logLevel = System.getProperty(CLIENT_LOG_LEVEL, CLIENT_LOG_LEVEL_DEFAULT);
        String logEncode = System.getProperty(CLIENT_LOG_ENCODE, CLIENT_LOG_ENCODE_DEFAULT);
    }

    /**
     * 创建日志对象
     * @param loggerName
     * @return
     */
    private static Logger createLogger(final String loggerName) {
        return AgentLoggerFactory.getLogger(loggerName);
    }

    /**
     * Getter method for property <tt>log</tt>.
     * @return property value of log
     */
    public static Logger getLog() {
        return log;
    }

    /**
     * Getter method for property <tt>monitorLog</tt>.
     * @return property value of monitorLog
     */
    public static Logger getMonitorLog() {
        return monitorLog;
    }
}
