package com.y5neko.amiya.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {

    // 控制台 INFO 日志
    private static final Logger infoLogger = LoggerFactory.getLogger("INFO_LOG");

    // 错误日志文件 ERROR
    private static final Logger errorLogger = LoggerFactory.getLogger("ERROR_LOG");

    // 开发者日志 DEV
    private static final Logger devLogger = LoggerFactory.getLogger("DEV_LOG");

    /** 输出 info 到控制台 */
    public static void info(String message) {
        infoLogger.info(message);
        System.out.println(message);
    }

    /** 输出 error 到错误日志 */
    public static void error(String message, Throwable t) {
        errorLogger.error(message, t);
    }

    /** 输出开发者日志 */
    public static void dev(String message) {
        devLogger.debug(message);
    }
}
