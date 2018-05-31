package proxyserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import se.util.Logger;
import se.util.http.ProgressiveProcess;

/**
 * This is a hack for the demo purposes and should be removed.
 *
 * Protocol: The crawler has to write a line to the this server for every
 * document it downloads.
 */
public class LinkReciever implements Runnable {

    HashMap<Long, SEUrl> urlMap;

    public LinkReciever(HashMap<Long, SEUrl> urlMap) {
        this.urlMap = urlMap;
    }

    @Override
    public void run() {
        try {
            ServerSocket servsock = new ServerSocket(0);
            Logger.ilog(Logger.HIGH, "The link reciever server is running on : " + servsock.getLocalPort());

            while (true) {
                Socket sock = servsock.accept();
                new Thread(new LinkAdder(sock, urlMap)).start();
            }
        } catch (IOException ex) {
            Logger.elog(0, ex.getMessage());
        }
    }
}
