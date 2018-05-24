
import java.io.IOException;
import java.util.LinkedHashMap;
import se.dscore.NodeProxy;
import se.dscore.Scheduler;
import se.util.Logger;

public class RBScheduler implements Scheduler {

    DMasterConfiguration config;
    static int wservers;
    static int prxyservers;

    public RBScheduler(DMasterConfiguration config) {
        this.config = config;
    }

    @Override
    public void schedule(String host,
            LinkedHashMap<String, NodeProxy> slaves) {

        Logger.ilog(Logger.LOW, "trying to schedule jobs");

        NodeProxy slv = slaves.get(host);
        if (slv.getProcessCount() < 2) {
            try {
                slv.createProcess(config.getDmgrExecutable(), config.getDmgrCommandLine());
                if(wservers < 1) {
                    slv.createProcess(config.getWsExecutable(), config.getWsCmdline());
                    wservers++;
                }
                if(prxyservers < 1) {
                    slv.createProcess(config.getPrxyExecutable(), config.getPrxyCmdline());
                    prxyservers++;
                }
            } catch (IOException ex) {
                Logger.elog(Logger.HIGH, "Error creating process on node [" + config.getDmgrExecutable() + " [" + host + "]");
            }
        }
    }
}
