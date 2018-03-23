package beats;


/*
 * This class can be extended to support the different kind of status which
 * may be specific to some components.
 */
class Status {
    
    private String status;
    private String startTime;
    private String pid;
    
    /*
     * Process the json here and assign values to attributed.
     */
    Status(String json) {
        
    }
    
    String getStatus() {
        return status;
    }
    
    String getStartTime() {
        return startTime;
    }
    
    String getPid() {
        return pid;
    }
    
    @Override
    public String toString() {
        return "STATUS : " + status + ", START_TIME : " + startTime + "PID : " + pid;
    }
}
