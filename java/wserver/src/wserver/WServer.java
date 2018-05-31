package wserver;

import java.io.IOException;
import java.util.HashMap;
import jsonparser.JsonException;
import se.dscore.SlaveProcess;
import se.ipc.ESocket;
import se.ipc.pdu.HiPDU;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.PDUConsts;
import se.util.Address;
import se.util.Logger;
import se.util.http.HttpServer;
import se.util.http.ProgressiveProcess;

public class WServer extends SlaveProcess implements ProgressiveProcess {

    WSView wsview;
    HashMap<String, Address> dmgrs;
    
    /* This is the facade to the user */
    static HttpServer hserver;

    @Override
    protected void handle_hello(ESocket sock, HiPDU hpdu) {
        Address addr;
        switch (hpdu.getWho()) {
            case PDUConsts.PN_DMGR:
                addr = new Address(sock.getHost(), hpdu.getRunningPort());
                dmgrs.put(addr.host + ":" + addr.port, addr);
                Logger.ilog(Logger.LOW, "A new dmgr found at " + addr);
                break;
            case PDUConsts.PN_PRXYSERVER:
                addr = new Address(sock.getHost(), hpdu.getHttpPort());
                Logger.ilog(Logger.LOW, "A new proxyserver found at " + addr);
                break;
        }
    }

    public static void main(String[] args) throws IOException, JsonException, InvalidPDUException {

        WSConfiguration conf
                = (WSConfiguration) ((args.length > 4)
                        ? new WSConfiguration(args[4])
                        : new WSConfiguration());

        conf.setTicket(args[0]);
        conf.setPid(args[1]);
        conf.setMasterHost(args[2]);
        conf.setMasterPort(Integer.parseInt(args[3]));
        conf.setProcessRole(PDUConsts.PN_WSERVER);

        WServer ws = new WServer(conf);
        ws.run();

        Logger.ilog(Logger.HIGH, "Http server running on : " + hserver.getPort());
    }

    public WServer(WSConfiguration config) throws IOException, JsonException, InvalidPDUException {
        super(config);
        dmgrs = new HashMap<>();
        wsview = new WSView(dmgrs);
        hserver = new HttpServer(config.getDocRoot());
        hserver.registerAPI("search", this);
    }

    @Override
    public Object getProgress() {
        return wsview;
    }
}
