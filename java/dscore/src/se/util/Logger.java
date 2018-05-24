package se.util;

import java.io.PrintStream;
import java.util.Date;

public class Logger {

    public static final int PROTO = 0;
    public static final int DEBUG = 1;
    public static final int LOW = 2;
    public static final int MEDIUM = 3;
    public static final int HIGH = 4;

    private static final String INFO = "INFO";
    private static final String ERROR = "ERROR";

    static PrintStream pw = System.out;
    static int loglevel;
    static String[] levels = {"PROTO", "DEBUG", "LOW", "MEDIUM", "HIGH"};

    public static void setLoglevel(int level) {
        loglevel = level;
        System.err.println("Loglevel is " + levels[loglevel]);
    }

    private static void log(int level, String type, String logent, Throwable e) {
        if (level < loglevel) {
            return;
        }
        pw.println(new Date().toString() + "\t" + type + "\t[" + Thread.currentThread().getName() + "]\t" + logent);
        if (e != null) {
            printException(e);
        }
    }

    public static void ilog(int level, String message) {
        log(level, INFO, message, null);
    }

    public static void elog(int level, String message) {
        log(level, ERROR, message, null);
    }

    public static void elog(int level, String message, Throwable e) {
        log(level, ERROR, message, e);
    }

    private static void printException(Throwable e) {
        StackTraceElement[] st = e.getStackTrace();
        for (StackTraceElement ste : st) {
            pw.println(ste);
        }
        Throwable cause = e.getCause();
        if(cause != null) {
            pw.println("caused by: ");
            printException(cause);
        }
    }

}
