package beats;


/**
 * This is an abstraction for a process which you are going to probe
 * it includes the methods to probe the particular process.
 * 
 * This is the same thing whose attributes are displayed in the UI.
 */
public class Process {
    
    /* alive flag can be used by the UI to decide whether to keep this
     * process's UI entry in the UI or not
     */
    boolean alive;
    
    String host;
    String port;
    Status status;
    
    public String toString() {
        return "HOST : " + host + ", PORT : " + port + ", " + status;
    }
    
    Process(String host, String port) {
        this.host = host;
        this.port = port;
    }
    
    void probe() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                probe_thread();
            }
        }, createThreadId()).start();
    }
    
    /* This provides a unique thread ID for debugging */
    private String createThreadId() {
        return host + ":" + port;
    }
    
    private void probe_thread() {
        
        alive = true;
        
        try {
            /* build a json string for the request */
            
            /* Create a Socket connection to (host, port) */
        
            /* Send the JSON. */
        
            /* Consume the JSON on the InputStream of the socket 
             * (JSON library has this support :) )*/
            
            /* Read the status from JSON and set status */
        
            
        } catch(Exception ex) {
            /* log the exception */
            alive = false;
        }
    }
    
}
