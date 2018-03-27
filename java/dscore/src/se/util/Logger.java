package se.util;

import java.io.PrintStream;
import java.util.Date;


public class Logger {
    
    public static final int DEBUG   = 1;
    public static final int LOW     = 2;
    public static final int MEDIUM  = 3;
    public static final int HIGH    = 4;
    
    private static final String INFO = "INFO";
    private static final String ERROR = "ERROR";
    
    static PrintStream pw = System.out;
    static int loglevel;
    static String[] levels = {"DEBUG", "LOW", "MEDIUM", "HIGH"};
    
    public static void setLoglevel(int level) {
        System.err.println("Loglevel is " + levels[loglevel]);
    }
    
    private static void log(int level, String type, String logent) {
        if(level >= loglevel)
            pw.println(new Date().toString() + "\t" + type + "\t[" + Thread.currentThread().getName() + "]\t" + logent);
    }
    
    public static void ilog(int level, String message) {
        log(level, INFO, message);
    }
    
    public static void elog(int level, String message) {
        log(level, ERROR, message);
    }
    
}
