package org.esec.mcg.utils.logger;

import org.esec.mcg.utils.SystemUtil;

/**
 * Created by yz on 2015/9/10.
 */
public final class LogUtils {

    private static Logger logger;
    static {
        logger = new Logger();
    }

    // 允许输出日志
    public static boolean configAllowLog = true;

    // 配置日志Tag前缀
    public static String configTagPrefix = "";

    /**
     * verbose的输出
     * @param msg
     * @param args
     */
    public static void v(String msg, Object... args) {
        logger.v(SystemUtil.getStackTrace(), msg, args);
    }
    public static void v(Object object) { logger.v(SystemUtil.getStackTrace(), object); }

    /**
     * debug的输出
     * @param msg
     * @param args
     */
    public static void d(String msg, Object... args) {
        logger.d(SystemUtil.getStackTrace(), msg, args);
    }
    public static void d(Object object) { logger.d(SystemUtil.getStackTrace(), object); }

    /**
     * info的输出
     * @param msg
     * @param args
     */
    public static void i(String msg, Object... args) {
        logger.d(SystemUtil.getStackTrace(), msg, args);
    }
    public static void i(Object object) { logger.i(SystemUtil.getStackTrace(), object); }

    /**
     * warn的输出
     * @param msg
     * @param args
     */
    public static void w(String msg, Object... args) {
        logger.w(SystemUtil.getStackTrace(), msg, args);
    }
    public static void w(Object object) { logger.w(SystemUtil.getStackTrace(), object); }

    /**
     * error的输出
     * @param msg
     * @param args
     */
    public static void e(String msg, Object... args) {
        logger.e(SystemUtil.getStackTrace(), msg, args);
    }
    public static void e(Object object) { logger.e(SystemUtil.getStackTrace(), object); }

    /**
     * assert的输出
     * @param msg
     * @param args
     */
    public static void wtf(String msg, Object... args) {
        logger.wtf(SystemUtil.getStackTrace(), msg, args);
    }
    public static void wtf(Object object) { logger.wtf(SystemUtil.getStackTrace(), object); }

    /**
     * 打印json
     * @param json
     */
    public static void json(String json) { logger.json(SystemUtil.getStackTrace(), json); }
}
