package wserver;

import dmgr.SearchResult;
import java.io.IOException;
import java.io.ObjectInputStream;
import se.ipc.ESocket;
import se.ipc.pdu.SearchPDU;
import se.util.Address;
import se.util.Logger;

public class SearchAgent extends Thread {

    SearchResult result;
    Address addr;
    SearchPDU spdu;

    public SearchAgent(Address addr, SearchPDU spdu) {
        this.addr = addr;
        this.spdu = spdu;
    }

    @Override
    public void run() {
        try {
            Logger.ilog(Logger.DEBUG, "Using diffrent protocol for dmgrs");
            ESocket sock = new ESocket(addr.host, addr.port);
            sock.send(spdu);
            result = (SearchResult) new ObjectInputStream(sock.getInputStream()).readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.elog(Logger.HIGH, "Error while reading data from dmgr at " + addr.toString() + " " + ex.getMessage());
        }
    }
    
    public SearchResult getResult() {
        return result;
    }
}
