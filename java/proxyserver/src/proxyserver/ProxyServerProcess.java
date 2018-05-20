package proxyserver;

import java.io.IOException;
import java.util.HashMap;
import jsonparser.JsonExposed;
import se.dscore.SlaveProcess;
import se.ipc.pdu.PDUConsts;
import se.util.Logger;
import se.util.http.ProgressiveProcess;

public class ProxyServerProcess extends SlaveProcess implements ProgressiveProcess {

    @JsonExposed(comment = "This is the URL map")
    public HashMap<Long, SEUrl> urlMap;
    
    ProxyServer hserver;
    
    public static void main(String[] args) throws IOException {
        ProxyConfiguration conf
                = (ProxyConfiguration) ((args.length > 6)
                        ? new ProxyConfiguration(args[6])
                        : new ProxyConfiguration());

        conf.setTicket(args[0]);
        conf.setPid(args[1]);
        conf.setErrFile(args[2]);
        conf.setOutFile(args[3]);
        conf.setMasterHost(args[4]);
        conf.setMasterPort(Integer.parseInt(args[5]));
        conf.setProcessRole(PDUConsts.PN_PRXYSERVER);

        ProxyServerProcess psp = new ProxyServerProcess(conf);
        psp.run();

        Logger.ilog(Logger.HIGH, "Http server running on : " + psp.hserver.getPort());
        psp.hserver.run();
    }

    public ProxyServerProcess(ProxyConfiguration config) throws IOException {
        super(config);
        urlMap = new HashMap<>();
        hserver = new ProxyServer(".", this, urlMap);
        setHttpPort(hserver.getPort());
        new Thread(new LinkReciever(urlMap)).start();
    }

    @Override
    public Object getProgress() {
        return this;
    }
}
