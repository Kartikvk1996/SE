package beats;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * This is a sample main class. Just copy/implement this to the UI's startup method.
 * 
 * The UI must allow the user to 
 * 
 *      Change the update_interval (Just call beats.updateInterval)
 *      Connect to master by specifying host and port
 *      Display the process states.
 * 
 */
public class SampleMain {
    
    Beats beats;
    private long UPDATE_INTERVALS = 2000;
    
    SampleMain(String args[]) {
        /**
         * Well you get some arguments to process
         * 
         * args[0]: Master IP.
         * args[1]: Master Port.
         * 
         * You need to register to the master. Which will tell all the processes
         * that they need to register. if no arguments are provided it means
         * This process was not created by the master. The user specifies the 
         * master connection details and then you should connect this beats
         * object to master. beats.registerToMaster() from UI inputs.
         * 
         * port = -1 if no args were specified.
         */
        
        int port = -1;
        String masterIP = "";
        
        if(args.length > 1) {
            masterIP = args[0];
            port = Integer.parseInt(args[1]);
        }
        
        /* create a beats object with args[0] and args[1] */
        beats = new Beats(masterIP, port);
        beats.setUpdateInterval(UPDATE_INTERVALS);
        
        /* Run the beats thread to probe the UI. */
        beats.run();
        
        /* We will update the UI from TimerTask. It will be fired from uiUpdateTimer */
        TimerTask uiUpdateTask = new TimerTask() {
            @Override
            public void run() {
                updateUI();
            }
        };
        
        /* This timer will update the UI after every UPDATE_INTERVAL milliseconds */
        Timer uiUpdateTimer = new Timer();
        uiUpdateTimer.scheduleAtFixedRate(uiUpdateTask, 0, UPDATE_INTERVALS);
        
        TLogger.log("Beats initialization complete");
    }
    
    /* Copy the body to end of UI startup code (depends on framework used) */
    public static void main(String args[]) {
        SampleMain smain = new SampleMain(args);
    }

    private void updateUI() {
        
        TLogger.log("UI Update triggered");
        
        for (Process process : beats.processes) {
            
            /* You can update the UI here as required. As of now try printing
             * things from here. Once the implementation is ready put UI stuff.*/
            System.out.println(process);
        }
    }
    
}
