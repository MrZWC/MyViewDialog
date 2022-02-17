package com.zwc.viewdialog;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * ClassName BackStackLog
 * User: zuoweichen
 * Date: 2022/2/16 11:58
 * Description: 描述
 */
public class BackStackLog {

    private static final InternalLibLog INTERNAL_LIB_LOG = new InternalLibLog(
            BuildConfig.LIB_NAME,
            BuildConfig.LIB_VERSION_NAME,
            BuildConfig.LIB_VERSION_CODE
    );

    /**
     * 设置日志级别，可取值为 {@linkplain Log#ERROR}, {@linkplain Log#WARN}, {@linkplain Log#INFO},
     * {@linkplain Log#DEBUG}, {@linkplain Log#VERBOSE}
     */
    public static void setLogLevel(int logLevel) {
        INTERNAL_LIB_LOG.setLogLevel(logLevel);
    }

    /**
     * 获取当前日志级别
     */
    public static int getLogLevel() {
        return INTERNAL_LIB_LOG.getLogLevel();
    }

    public static void v(@Nullable String msg, Object... args) {
        v(null, msg, args);
    }

    public static void v(@NonNull Throwable e) {
        v(e, null);
    }

    public static void v(@Nullable Throwable e, @Nullable String msg, Object... args) {
        INTERNAL_LIB_LOG.print(Log.VERBOSE, e, msg, args);
    }

    public static void d(@Nullable String msg, Object... args) {
        d(null, msg, args);
    }

    public static void d(@NonNull Throwable e) {
        d(e, null);
    }

    public static void d(@Nullable Throwable e, @Nullable String msg, Object... args) {
        INTERNAL_LIB_LOG.print(Log.DEBUG, e, msg, args);
    }

    public static void i(@Nullable String msg, Object... args) {
        i(null, msg, args);
    }

    public static void i(@NonNull Throwable e) {
        i(e, null);
    }

    public static void i(@Nullable Throwable e, @Nullable String msg, Object... args) {
        INTERNAL_LIB_LOG.print(Log.INFO, e, msg, args);
    }

    public static void w(@Nullable String msg, Object... args) {
        w(null, msg, args);
    }

    public static void w(@NonNull Throwable e) {
        w(e, null);
    }

    public static void w(@Nullable Throwable e, @Nullable String msg, Object... args) {
        INTERNAL_LIB_LOG.print(Log.WARN, e, msg, args);
    }

    public static void e(@Nullable String msg, Object... args) {
        e(null, msg, args);
    }

    public static void e(@NonNull Throwable e) {
        e(e, null);
    }

    public static void e(@Nullable Throwable e, @Nullable String msg, Object... args) {
        INTERNAL_LIB_LOG.print(Log.ERROR, e, msg, args);
    }

    /**
     * Lib 内的统一日志输出
     *
     * @author idonans
     * @version 1.2
     */
    private static class InternalLibLog {

        /**
         * 日志打印级别, 默认 {@linkplain Log#ERROR}
         */
        private int mLogLevel = Log.ERROR;
        private final String mLogTag;

        private InternalLibLog(final String libName, String libVersionName, int libVersionCode) {
            final String logTag = libName + "_" + libVersionName + "(" + libVersionCode + ")";
            final int length = logTag.length();
            if (length > 23) {
                mLogTag = logTag.substring(0, 23);
            } else {
                mLogTag = logTag;
            }
        }

        /**
         * 设置日志级别，可取值为 {@linkplain Log#ERROR}, {@linkplain Log#WARN}, {@linkplain Log#INFO},
         * {@linkplain Log#DEBUG}, {@linkplain Log#VERBOSE}
         */
        public void setLogLevel(int logLevel) {
            mLogLevel = logLevel;
        }

        /**
         * 获取当前日志级别
         */
        public int getLogLevel() {
            return mLogLevel;
        }

        private boolean isLoggable(int logLevel) {
            return logLevel >= mLogLevel;
        }

        private void print(int logLevel, @Nullable Throwable e, @Nullable String msg, Object... args) {
            if (!isLoggable(logLevel)) {
                return;
            }

            if (e != null) {
                if (logLevel <= Log.VERBOSE) {
                    Log.v(mLogTag, formatMessage(msg, args), e);
                } else if (logLevel <= Log.DEBUG) {
                    Log.d(mLogTag, formatMessage(msg, args), e);
                } else if (logLevel <= Log.INFO) {
                    Log.i(mLogTag, formatMessage(msg, args), e);
                } else if (logLevel <= Log.WARN) {
                    Log.w(mLogTag, formatMessage(msg, args), e);
                } else {
                    Log.e(mLogTag, formatMessage(msg, args), e);
                }
                return;
            }

            if (logLevel <= Log.VERBOSE) {
                Log.v(mLogTag, formatMessage(msg, args));
            } else if (logLevel <= Log.DEBUG) {
                Log.d(mLogTag, formatMessage(msg, args));
            } else if (logLevel <= Log.INFO) {
                Log.i(mLogTag, formatMessage(msg, args));
            } else if (logLevel <= Log.WARN) {
                Log.w(mLogTag, formatMessage(msg, args));
            } else {
                Log.e(mLogTag, formatMessage(msg, args));
            }
        }

        @NonNull
        private String formatMessage(@Nullable String msg, Object... args) {
            if (msg == null) {
                return "null";
            }
            if (args != null) {
                return String.format(msg, args);
            }
            return msg;
        }

    }
}
