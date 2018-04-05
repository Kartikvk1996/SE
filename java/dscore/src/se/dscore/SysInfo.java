package se.dscore;

import java.lang.reflect.Field;
import java.util.Date;
import jsonparser.DictObject;
import jsonparser.JsonAssignable;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;
import se.util.Logger;

public class SysInfo extends JsonAssignable {

    @JsonExposed(comment = "Number of cores in the slave machine")
    public int cores;
    
    @JsonExposed(comment = "Available memory in the slave machine")
    public long availableMemory;
    
    @JsonExposed(comment = "Free memory from available JVM memory")
    public long freeMemory;
    
    @JsonExposed(comment = "The startup time of the slave")
    public long startTime;
    
    @JsonExposed(comment = "Operating system of the Node")
    public String osType;
    
    @JsonExposed(comment = "Java version installed on the node")
    public String javaVersion;
    
    private static int s_cores;
    private static long s_availableMemory;
    private static long s_freeMemory;
    private static long s_startTime;
    private static String s_osType;
    private static String s_javaVersion;
    
    static {
        s_cores = Runtime.getRuntime().availableProcessors();
        s_freeMemory = Runtime.getRuntime().freeMemory();
        s_availableMemory = Runtime.getRuntime().totalMemory();
        s_startTime = new Date().getTime();
        s_osType = System.getProperty("os.name");
        s_javaVersion = System.getProperty("java.version");
    }
    
    public SysInfo(boolean neverUsed) {
        this.cores = s_cores;
        this.availableMemory = s_availableMemory;
        this.freeMemory = s_freeMemory;
        this.startTime = s_startTime;
        this.javaVersion = s_javaVersion;
        this.osType = s_osType;
    }
    
    public SysInfo() {
        /* just to skip the above assignments */
    }
    
    public int getCores() { return cores; }
    public long getAvailableMem() { return availableMemory; }
    public long getfreeMemory() { return freeMemory; }
    public long getStartTime() { return startTime; }
    public String getOsType() { return osType; }
    public String getJavaVersion() { return javaVersion; }
    
    @Override
    public Object initialize(JsonObject jObject) {
        
        SysInfo info = new SysInfo(true);
        
        for (Field field : getClass().getFields()) {
            try {
                field.set(this, ((DictObject)jObject).get(field.getName()).getValue());
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.elog(Logger.MEDIUM, "Couldn't initialize the SysInfo Object " + ex.getMessage());
            }
        }
        return info;
    }
}
