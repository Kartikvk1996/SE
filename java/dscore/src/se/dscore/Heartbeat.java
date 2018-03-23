package se.dscore;

import java.io.IOException;
import se.util.Logger;

public class Heartbeat implements Runnable {

    MasterProxy mproxy;
    Node node;
    int millis;
    public Heartbeat(MasterProxy mproxy, Node self, int millis) {
        this.mproxy = mproxy;
        node = self;
        this.millis = millis;
    }
    
    @Override
    public void run() {
        while(true) {
            try {
                mproxy.send(node.getStatus());
            } catch (IOException ex) {
                Logger.elog(Logger.HIGH, "Unable to send heartbeat");
            }
            try { Thread.sleep(millis); } catch (InterruptedException ex) { }
        }
    }
    
}
