package se.util;

import java.io.PrintStream;
import java.util.Date;


public class Logger {
    
    public static final int LOW = 0;
    public static final int MEDIUM = 1;
    public static final int HIGH = 2;
    
    private static final String INFO = "INFO";
    private static final String ERROR = "ERROR";
    
    static PrintStream pw = System.out;
    
    private static void log(int level, String type, String logent) {
        if(level != LOW)
            pw.println(new Date().toString() + "\t" + type + "\t" + logent);
    }
    
    public static void ilog(int level, String message) {
        log(level, INFO, message);
    }
    
    public static void elog(int level, String message) {
        log(level, ERROR, message);
    }
    
}
