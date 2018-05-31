package proxyserver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import jsonparser.JsonExposed;
import se.dscore.SlaveProcess;
import se.ipc.pdu.PDUConsts;
import se.util.http.HttpRequest;
import se.util.http.ProgressiveProcess;
import se.util.http.RestServlet;

public class ProxyServerProcess extends SlaveProcess implements ProgressiveProcess, RestServlet {

    @JsonExposed(comment = "This is the URL map")
    public HashMap<Long, SEUrl> urlMap;
            
    public void serve(HttpRequest req) throws IOException {
        switch (req.getUrl()) {
            case "proxy":
                Long index = Long.parseLong(req.getData());
                SEUrl seu = urlMap.get(index);
                seu.hits++;
                OutputStream out = req.getOutputStream();
                out.write(("HTTP/1.0 302 Found\n" +
                           "Location: " + seu.url + 
                           "\n\n").getBytes());
                break;
        }
    }
    
    public static void main(String[] args) throws IOException {
        ProxyConfiguration conf
                = (ProxyConfiguration) ((args.length > 4)
                        ? new ProxyConfiguration(args[4])
                        : new ProxyConfiguration());

        conf.setTicket(args[0]);
        conf.setPid(args[1]);
        conf.setMasterHost(args[2]);
        conf.setMasterPort(Integer.parseInt(args[3]));
        conf.setProcessRole(PDUConsts.PN_PRXYSERVER);

        ProxyServerProcess psp = new ProxyServerProcess(conf);
        psp.run();

    }

    @Override
    public void run() {
        registerAPI("proxy", this);
        new Thread(new LinkReciever(urlMap)).start();
        super.run();
    }
    
    public ProxyServerProcess(ProxyConfiguration config) throws IOException {
        super(config);
        urlMap = new HashMap<>();
    }

    @Override
    public Object getProgress() {
        return this;
    }
}
