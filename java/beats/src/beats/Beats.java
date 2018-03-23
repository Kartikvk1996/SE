package beats;

import java.util.ArrayList;


/* This guy listens on a particular port to recieve connections 
 * and creates a thread per process to get probe and  */
public class Beats {

    String masterIP;
    int masterPort;
    int listeningPort;
    long updateInterval;
    ArrayList<Process> processes;
    
    Beats(String masterIP, int masterPort) {
        
        this.masterIP = masterIP;
        this.masterPort = masterPort;
        
        processes = new ArrayList<>();
        
        /* Create a listener thread */
        new Thread(new Runnable() {
            @Override
            public void run() {
                listen();
            }
        }).start();
        
    }
    
    void setUpdateInterval(long updateInterval) {
        TLogger.log("updateInterval set to " + updateInterval);
        this.updateInterval = updateInterval;
    }
    
    void setMasterIP(String ip) {
        this.masterIP = ip;
    }
    
    void setMasterPort(int port) {
        this.masterPort = port;
    }
    
    /* This thread will run forever probing the processes at regular intervals*/
    void run() {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                for (Process process : processes) {
                    process.probe();
                }
                TLogger.log("Probing requests sent");
                try { Thread.sleep(updateInterval); }
                catch(InterruptedException ex) { /* don't care */}
            }
        })).start();        
    }
    
    /* This was kept as a seperate method as registering to master can be 
     * done in two ways. 
     * 1. From UI
     * 2. Programatically at startup.
     */
    void registerToMaster() {
        
        TLogger.log("Connecting to master @(" + masterIP + ", " + masterPort + ")");
        
        /* Check whether the masterPort is valid. (masterPort>-1) else return */
        if(masterPort < 0)
            return;
        
        /* Register to master by creating a JSON object and send it to master */
        
    }
    
    void listen() {
        
        /* Open a ServerSocket and listen to connections */
        
        
        /* Register your self to the master. */
        
        while(true) {
            
            /* Accept a connection */
            
            /* Parse the JSON to get the host port from the stream. */
            
            /* Create a process object and add to processes list. */
            
            /* Close the connection */
            
        }
        /* Never return from here :) */
    }
    
}
