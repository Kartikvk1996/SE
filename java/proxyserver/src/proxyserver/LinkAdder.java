package proxyserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import se.util.Logger;

public class LinkAdder implements Runnable {

    Socket sock;
    HashMap<Long, SEUrl> urlMap;

    public LinkAdder(Socket sock, HashMap<Long, SEUrl> urlMap) {
        this.sock = sock;
        this.urlMap = urlMap;
    }

    @Override
    public void run() {
        BufferedReader rdr = null;
        try {
            rdr = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String line = rdr.readLine();
            Logger.ilog(Logger.DEBUG, line);
            if (line == null) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
            } else {
                try {
                    String chunks[] = line.split("\t");
                    urlMap.put(Long.parseLong(chunks[0]), new SEUrl(chunks[1], 0));
                } catch (Exception e) {
                    Logger.elog(Logger.HIGH, "Error occured while recieving data from the link server ", e);
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(LinkReciever.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rdr.close();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(LinkReciever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
