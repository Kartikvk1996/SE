package se.dscore;

import se.util.Logger;
import se.ipc.EServerSocket;
import se.ipc.ESocket;
import java.io.IOException;


public class Server {
    
    private EServerSocket servSocket;
    private RequestHandler handler;
    
    
    public Server(EServerSocket serverSock) {
        this.servSocket = serverSock;
    }
    
    public Server(RequestHandler handler) throws IOException {
        this.servSocket = new EServerSocket(0);
        this.handler = handler;
    }
    
    public Server(int port, RequestHandler handler) throws IOException {
        this.servSocket = new EServerSocket(port);
        this.handler = handler;
    }
    
    public void run() throws IOException {
        while(true) {
            ESocket sock = servSocket.accept();
            new Thread(() -> {
                try {
                    handler.handle(sock);
                } catch (IOException ex) {
                    Logger.elog(Logger.HIGH, ex.getMessage());
                }
            }).start();
            Logger.ilog(Logger.LOW, "Accepted a connection");
        }
    }
 
    public String getHost() {
        return servSocket.getHost();
    }
    
    public int getPort() {
        return servSocket.getPort();
    }
}
