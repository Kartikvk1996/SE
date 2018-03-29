package se.ipc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import se.util.Logger;

public class EServerSocket {

    ServerSocket socket;
    
    public ESocket accept() throws IOException {
        return new ESocket(socket.accept());
    }
    
    public EServerSocket(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    public String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.elog(Logger.HIGH, "unable to get the localhost ip");
        }
        return "localhost";
    }

    public int getPort() {
        return socket.getLocalPort();
    }
    
}
