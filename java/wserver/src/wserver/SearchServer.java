package wserver;

import dmgr.SearchResult;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import jsonparser.Json;
import jsonparser.JsonException;
import se.ipc.pdu.SearchPDU;
import se.util.Address;
import se.util.Logger;
import se.util.http.HttpRequest;
import se.util.http.RESTServer;

public class SearchServer extends RESTServer {

    private final WSView status;
    private final HashMap<String, Address> dmgrs;
    private Address prxyServer;

    public SearchServer(String docRoot, WServer ws, HashMap<String, Address> dmgrs) throws IOException {
        super(docRoot, ws);
        this.dmgrs = dmgrs;
        status = (WSView) ws.getProgress();
    }

    @Override
    protected void serve(HttpRequest req) throws IOException {

        switch (req.getUrl()) {
            case "search":
                handleSearch(req);
                break;
            default:
                super.serve(req);
        }
    }

    public void setProxyServer(Address addr) {
        this.prxyServer = addr;
    }
    
    private void handleSearch(HttpRequest req) throws IOException {

        Logger.ilog(Logger.LOW, "Search request. " + req.getData());
        Logger.ilog(Logger.DEBUG, "There are " + dmgrs.size() + " dmgrs");

        status.queries++;

        try (OutputStream out = req.getOutputStream()) {

            if (prxyServer == null) {
                Logger.elog(Logger.HIGH, "No proxyserver registered. Please run a proxy server");
                out.write("No proxyserver registered. Please run a proxy server\n\n".getBytes());
                out.close();
                return;
            }

            long start = System.currentTimeMillis();
            ArrayList<SearchResult> results = new ArrayList<>();
            SearchAgent[] aThreads = new SearchAgent[dmgrs.size()];
            SearchPDU spdu = new SearchPDU(req.getData());
            int i = 0;
            for (Address addr : dmgrs.values()) {
                aThreads[i] = new SearchAgent(addr, spdu);
                aThreads[i].start();
            }

            for (SearchAgent aThread : aThreads) {
                try {
                    aThread.join();
                } catch (InterruptedException ex) {
                    Logger.elog(Logger.MEDIUM, "Something went wrong while waiting for results. " + ex.getMessage());
                }
                results.add(aThread.getResult());
            }

            long end = System.currentTimeMillis();

            SearchResponse resp = new SearchResponse((end - start), results, prxyServer);

            String str = "";
            try {
                str = Json.dump(resp);
            } catch (JsonException ex) {
                Logger.elog(Logger.HIGH, "Couldn't dump as JSON");
            }
            out.write("HTTP/1.0 200 OK\n".getBytes());
            out.write(("Content-Length: " + str.length() + "\n\n").getBytes());
            out.write(str.getBytes());

            status.squeries++;
        }
    }
}
