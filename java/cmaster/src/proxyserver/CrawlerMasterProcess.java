package proxyserver;

import java.io.IOException;
import java.util.ArrayList;
import jsonparser.JsonExposed;
import se.dscore.SlaveProcess;
import se.ipc.pdu.PDUConsts;
import se.util.Logger;
import se.util.http.ProgressiveProcess;

public class CrawlerMasterProcess extends SlaveProcess implements ProgressiveProcess {

    Process pyproc;
    
    @JsonExposed(comment = "This is the URL map")
    public ArrayList<SEUrl> urlMap;

    CMasterServer hserver;
    LinkReciever lrcvr;

    public static void main(String[] args) throws IOException {
        CMasterConfiguration conf
                = (CMasterConfiguration) ((args.length > 6)
                        ? new CMasterConfiguration(args[6])
                        : new CMasterConfiguration());

        conf.setTicket(args[0]);
        conf.setPid(args[1]);
        conf.setErrFile(args[2]);
        conf.setOutFile(args[3]);
        conf.setMasterHost(args[4]);
        conf.setMasterPort(Integer.parseInt(args[5]));
        conf.setProcessRole(PDUConsts.PN_PRXYSERVER);

        CrawlerMasterProcess psp = new CrawlerMasterProcess(conf);
        psp.run();

        Logger.ilog(Logger.HIGH, "Http server running on : " + psp.hserver.getPort());
        psp.hserver.run();
    }

    public CrawlerMasterProcess(CMasterConfiguration config) throws IOException {
        super(config);
        urlMap = new ArrayList<>();
        hserver = new CMasterServer(".", this, urlMap);
        setHttpPort(hserver.getPort());
        lrcvr = new LinkReciever(urlMap);
        new Thread(lrcvr).start();
    }

    @Override
    public void run() {
        super.run();
        /* start the python process It has following arguments
         * python <master-host> <master-port>
         */
        String[] cmd = {"python", "127.0.0.1", getPort() + ""};
        try {
            pyproc = Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            Logger.elog(Logger.HIGH, "Python crawler process creation failed ", ex);
        }
    }

    @Override
    public Object getProgress() {
        return this;
    }
}
