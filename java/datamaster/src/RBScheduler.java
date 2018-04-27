
import java.io.IOException;
import java.util.LinkedHashMap;
import se.dscore.NodeProxy;
import se.dscore.Scheduler;
import se.util.Logger;

public class RBScheduler implements Scheduler {

    DMasterConfiguration config;
    private final String mhost;
    private final int mport;

    public RBScheduler(DMasterConfiguration config, String mhost, int mport) {
        this.config = config;
        this.mhost = mhost;
        this.mport = mport;
    }

    @Override
    public void schedule(String host,
            LinkedHashMap<String, NodeProxy> slaves) {

        Logger.ilog(Logger.LOW, "trying to schedule jobs");

        NodeProxy slv = slaves.get(host);
        if (slv.getProcessCount() < 2) {
            try {
                slv.createProcess(config.getDmgrExecutable(), config.getDmgrCommandLine());
                slv.createProcess(config.getDmgrExecutable(), config.getDmgrCommandLine());
                slv.createProcess(config.getDmgrExecutable(), config.getDmgrCommandLine());
            } catch (IOException ex) {
                Logger.elog(Logger.HIGH, "Error creating process on node [" + config.getDmgrExecutable() + " [" + host + "]");
            }
        }
    }
}
