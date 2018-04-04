package se.dscore;

/**
 * A proxy object for the Master where you can send and recv PDU's
 */

import java.io.IOException;
import jsonparser.JsonException;
import se.ipc.ESocket;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.PDU;

public class MasterProxy {
    private final String host;
    private final int port;
    
    private int httpPort;
    private long jarVersion;

    public MasterProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public String getHost(){
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public PDU send(PDU pdu, boolean rcvBack) throws IOException, JsonException, InvalidPDUException {
        ESocket sock = new ESocket(host, port);
        sock.send(pdu);
        if(rcvBack)
            return sock.recvPDU();
        return null;
    }
    
    public void setHttpPort(int port) {
        this.httpPort = port;
    }
    
    public void setJarVersion(long jarVersion) {
        this.jarVersion = jarVersion;
    }
    
    public long getJarVersion() {
        return jarVersion;
    }
    
    public int getHttpPort() {
        return httpPort;
    }
}
