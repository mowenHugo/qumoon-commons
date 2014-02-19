package com.qumoon.commons;

/**
 * @author kevin
 */

import com.google.common.base.Throwables;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 关于异常的工具类.
 *
 * @author kevin
 */
public class Exceptions {

    /**
     * 将CheckedException转换为UncheckedException.
     */
    public static RuntimeException unchecked(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

    /**
     * 将ErrorStack转化为String.
     */
    public static String getStackTraceAsString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 判断异常是否由某些底层的异常引起.
     */
    public static boolean isCausedBy(Exception ex, Class<? extends Exception>... causeExceptionClasses) {
        Throwable cause = ex;
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeExceptionClasses) {
                if (causeClass.isInstance(cause)) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }

    public static <EX extends Exception> void logAndThrow(Logger logger, EX ex) throws EX {
        logger.error(Throwables.getStackTraceAsString(ex));
        throw ex;
    }
    public static <EX extends Exception> void logAndThrow(Logger logger, String extraMsg, EX ex) throws EX {
        logger.error("msg[{}] ex[{}]", extraMsg, Throwables.getStackTraceAsString(ex));
        throw ex;
    }
}
