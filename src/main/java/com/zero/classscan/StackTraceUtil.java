package com.zero.classscan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author jiachun.fjc
 */
public final class StackTraceUtil {

    public static String stackTrace(Throwable t) {
        if (t == null) {
            return "null";
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PrintStream ps = new PrintStream(out);
            t.printStackTrace(ps);
            ps.flush();
            return new String(out.toByteArray());
        } finally {
            try {
                out.close();
            } catch (IOException ignored) {
                // ignored
            }
        }
    }

    private StackTraceUtil() {
    }
}
