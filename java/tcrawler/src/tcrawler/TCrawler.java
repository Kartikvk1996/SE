package tcrawler;

import java.io.IOException;
import se.dscore.SlaveProcess;
import se.dscore.SlaveProcessConfiguration;
import se.util.Logger;

public class TCrawler extends SlaveProcess {

    public TCrawler(SlaveProcessConfiguration config) 
            throws IOException, ArrayIndexOutOfBoundsException {
        super(config);
    }

    /* Do nothing just start the python process out there */
    public static void main(String[] args) throws IOException {

        if (args.length < 6) {
            Logger.elog(Logger.HIGH, "The arguments must be <Ticket> <PID> <ERRFILE> <OUTFILE> <MHOST> <MPORT> [configFile]");
            System.exit(0);
        }
        
        SlaveProcessConfiguration conf = new SlaveProcessConfiguration();
        conf.setTicket(args[0]);
        conf.setPid(args[1]);
        conf.setErrFile(args[2]);
        conf.setOutFile(args[3]);
        conf.setMasterHost(args[4]);
        conf.setMasterPort(Integer.parseInt(args[5]));

        TCrawler crawler = new TCrawler(conf);
        crawler.run();

        /* start the python process */
        String[] cmd = new String[args.length - 5];
        cmd[0] = "python";
        for (int i = 6; i < args.length; i++) {
            cmd[i - 5] = args[i];
        }
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.start();
    }
}
