package tcrawler;

import java.io.IOException;
import se.dscore.SlaveProcess;
import se.dscore.SlaveProcessConfiguration;
import se.ipc.ESocket;
import se.ipc.pdu.HiPDU;
import se.ipc.pdu.PDUConsts;
import se.util.Logger;

public class CrawlerRunner extends SlaveProcess {

    Process pyproc;
    
    @Override
    protected void handle_hello(ESocket sock, HiPDU hpdu) {
        if (hpdu.getWho().equals(PDUConsts.PN_CMASTER)) {
            startPyJob(sock.getHost(), hpdu.getHttpPort());
        }
    }

    @Override
    public void deinit() {
        pyproc.destroy();
    }
    
    public CrawlerRunner(SlaveProcessConfiguration config)
            throws IOException, ArrayIndexOutOfBoundsException {
        super(config);
    }

    /* Do nothing just start the python process here */
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

        CrawlerRunner crunner = new CrawlerRunner(conf);
        crunner.run();

        /* you have no work honey. */
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void startPyJob(String host, int httpPort) {
        /* start the python process It has following arguments
         * python <master-host> <master-port>
         */
        String[] cmd = {"python", host, httpPort+""};
        try {
            pyproc = Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            Logger.elog(Logger.HIGH, "Python crawler process creation failed ", ex);
        }
    }
}
