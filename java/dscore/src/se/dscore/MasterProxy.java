package se.dscore;

import java.io.IOException;
import jsonparser.JsonException;
import se.ipc.ESocket;
import se.ipc.pdu.PDU;

public class MasterProxy {
    private final String host;
    private final int port;

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
    
    public void send(PDU pdu) throws IOException, JsonException {
        new ESocket(host, port).send(pdu);
    }
}
