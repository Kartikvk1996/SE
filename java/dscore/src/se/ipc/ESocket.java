package se.ipc;

import se.ipc.pdu.PDU;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ESocket {

    private Socket socket;

    public ESocket(String host, int port) throws IOException {
        this.socket = new Socket(host, port);                
    }

    ESocket(Socket accept) {
        this.socket = accept;
    }

    public void send(PDU iPDU) throws IOException {
        socket.getOutputStream().write(iPDU.toString().getBytes());
        socket.getOutputStream().flush();
    }

    public String getHost() {
        return socket.getInetAddress().getHostName();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public int getPort() {
        return socket.getPort();
    }

    public String readData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void close() throws IOException {
        socket.close();
    }
    
}
