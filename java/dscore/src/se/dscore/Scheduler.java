package se.dscore;

import java.io.IOException;
import java.util.LinkedHashMap;
import se.ipc.Consts;
import se.util.Logger;

public class Scheduler {

    void schedule(String host, LinkedHashMap<String, NodeProxy> slaves) throws IOException {
         /* we need some load-balancing kind of algorithm here. 
	 * For the time being we will use a simple algorithm a fixed
	 * set of processes per machine.
         */
        Logger.ilog(Logger.LOW, "trying to schedule jobs");

        NodeProxy slv = slaves.get(host);
        if (slv.getProcessCount() < 2) {
            //slv.createProcess("crawler");
            slv.createProcess(Consts.DMGR_BIN);
            slv.createProcess(Consts.DMGR_BIN);
            slv.createProcess(Consts.DMGR_BIN);
        }
    }
    
}
