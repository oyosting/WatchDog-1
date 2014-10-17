package in.mings.mingle.utils;

import android.util.Log;

public class Logger {
    public static final boolean LOGD = true;

    public static final int VERBOSE = Log.VERBOSE;

    /**
     * Priority constant for the println method; use {@link Log#DEBUG}
     */
    public static final int DEBUG = Log.DEBUG;

    /**
     * Priority constant for the println method; use {@link Log#INFO}
     */
    public static final int INFO = Log.INFO;

    /**
     * Priority constant for the println method; use {@link Log#WARN}
     */
    public static final int WARN = Log.WARN;

    /**
     * Priority constant for the println method; use {@link Log#ERROR}
     */
    public static final int ERROR = Log.ERROR;

    /**
     * Priority constant for the println method. use {@link Log#ASSERT}
     */
    public static final int ASSERT = Log.ASSERT;


    public static void v(String tag, String format, Object... args) {
        Log.v(tag, logFormat(format, args));
    }

    public static void d(String tag, String format, Object... args) {
        Log.d(tag, logFormat(format, args));
    }

    public static void i(String tag, String format, Object... args) {
        Log.i(tag, logFormat(format, args));
    }

    public static void w(String tag, String format, Object... args) {
        Log.w(tag, logFormat(format, args));
    }

    public static void e(String tag, String format, Object... args) {
        Log.e(tag, logFormat(format, args));
    }

    public static void e(String tag, Throwable tr, String format, Object... args) {
        Log.e(tag, logFormat(format, args), tr);
    }

    public static void e(String tag, Throwable tr) {
        Log.e(tag, "", tr);
    }

    public static boolean isLoggable(String tag, int level) {
        return Log.isLoggable(tag, level);
    }

    private static String prettyArray(String[] array) {
        if (array.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        int len = array.length - 1;
        for (int i = 0; i < len; i++) {
            sb.append(array[i]);
            sb.append(", ");
        }
        sb.append(array[len]);
        sb.append("]");

        return sb.toString();
    }

    /**
     * Format the arguments
     *
     * @param format
     * @param args
     * @return the formatted string
     */
    private static String logFormat(String format, Object... args) {
        if (args.length == 0)
            return format == null ? "null" : format;
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String[]) {
                args[i] = prettyArray((String[]) args[i]);
            }
        }
        String s = String.format(format, args);
//		s = "[" + Thread.currentThread().getId() + "] " + s;
        return s;
    }
}
