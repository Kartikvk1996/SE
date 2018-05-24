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
import se.util.http.ProgressiveProcess;

public class WServer extends SlaveProcess implements ProgressiveProcess {

    WSView wsview;

    HashMap<String, Address> dmgrs;
    SearchServer hserver;

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
                hserver.setProxyServer(addr);
                Logger.ilog(Logger.LOW, "A new proxyserver found at " + addr);
                break;
        }
    }

    public static void main(String[] args) throws IOException, JsonException, InvalidPDUException {

        WSConfiguration conf
                = (WSConfiguration) ((args.length > 6)
                        ? new WSConfiguration(args[6])
                        : new WSConfiguration());

        conf.setTicket(args[0]);
        conf.setPid(args[1]);
        conf.setErrFile(args[2]);
        conf.setOutFile(args[3]);
        conf.setMasterHost(args[4]);
        conf.setMasterPort(Integer.parseInt(args[5]));
        conf.setProcessRole(PDUConsts.PN_WSERVER);

        WServer ws = new WServer(conf);
        ws.run();

        Logger.ilog(Logger.HIGH, "Http server running on : " + ws.hserver.getPort());
        ws.hserver.run();
    }

    public WServer(WSConfiguration config) throws IOException, JsonException, InvalidPDUException {
        super(config);
        dmgrs = new HashMap<>();
        wsview = new WSView(dmgrs);
        hserver = new SearchServer(config.getDocRoot(), this, dmgrs);
        setHttpPort(hserver.getPort());
    }

    @Override
    public Object getProgress() {
        return wsview;
    }
}
