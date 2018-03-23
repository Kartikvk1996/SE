package se.ipc;

import java.io.IOException;
import java.net.ServerSocket;

public class EServerSocket {

    ServerSocket socket;
    
    public ESocket accept() throws IOException {
        return new ESocket(socket.accept());
    }
    
    public EServerSocket(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    public String getHost() {
        return socket.getInetAddress().getHostName();
    }

    public int getPort() {
        return socket.getLocalPort();
    }
    
}
