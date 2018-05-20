package proxyserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import se.util.Logger;
import se.util.http.ProgressiveProcess;

/**
 * This is a hack for the demo purposes and should be removed.
 *
 * Protocol: The crawler has to write a line to the this server for every
 * document it downloads.
 */
public class LinkReciever implements Runnable {

    ArrayList<SEUrl> urlMap;
    ServerSocket servsock;

    public LinkReciever(ArrayList<SEUrl> urlMap) {
        this.urlMap = urlMap;
    }

    @Override
    public void run() {
        try {
            ServerSocket servsock = new ServerSocket(0);
            Logger.ilog(Logger.HIGH, "The link reciever server is running on : " + servsock.getLocalPort());
            Socket sock = servsock.accept();
            BufferedReader rdr = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            while (true) {
                String line = rdr.readLine();
                Logger.ilog(Logger.DEBUG, line);
                if (line == null) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                    }
                } else {
                    urlMap.add(new SEUrl(line, 0));
                }
            }
        } catch (IOException ex) {
            Logger.elog(0, ex.getMessage());
        }
    }

    public static void xmain(String[] args) throws IOException {
        ArrayList<SEUrl> map = new ArrayList<>();
        Thread t = new Thread(new LinkReciever(map));
        CMasterServer ps = new CMasterServer(".", new ProgressiveProcess() {
            @Override
            public Object getProgress() {
                return "";
            }
        }, map);
        t.start();
        System.out.println("Http server listening on : " + ps.getPort());
        ps.run();
    }

    public int getPort() {
        return servsock.getLocalPort();
    }
}
