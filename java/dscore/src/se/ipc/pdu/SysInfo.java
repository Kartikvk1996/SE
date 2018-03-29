package se.ipc.pdu;

import java.lang.reflect.Field;
import java.util.Date;
import jsonparser.DictObject;
import jsonparser.JsonAssignable;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;
import se.util.Logger;

public class SysInfo extends JsonAssignable {

    @JsonExposed public int cores;
    @JsonExposed public long availableMemory;
    @JsonExposed public long freeMemory;
    @JsonExposed public long startTime;
    
    private static int s_cores;
    private static long s_availableMemory;
    private static long s_freeMemory;
    private static long s_startTime;
    
    static {
        s_cores = Runtime.getRuntime().availableProcessors();
        s_freeMemory = Runtime.getRuntime().freeMemory();
        s_availableMemory = Runtime.getRuntime().totalMemory();
        s_startTime = new Date().getTime();
    }
    
    public SysInfo(boolean neverUsed) {
        this.cores = s_cores;
        this.availableMemory = s_availableMemory;
        this.freeMemory = s_freeMemory;
        this.startTime = s_startTime;
    }
    
    public SysInfo() {
        /* just to skip the above assignments */
    }
    
    public int getCores() { return cores; }
    public long getAvailableMem() { return availableMemory; }
    public long getfreeMemory() { return freeMemory; }
    public long getStartTime() { return startTime; }
    
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
