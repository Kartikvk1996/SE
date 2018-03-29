package se.dscore;

import java.io.IOException;
import jsonparser.JsonExposed;
import se.ipc.ESocket;
import se.ipc.pdu.KillPDU;
import se.ipc.pdu.PDU;

public class Process {
    
    @JsonExposed(comment = "PID of the process. It's a string")
    public String pid;
    
    @JsonExposed(comment = "This denotes the type of process")
    public String type;
    
    @JsonExposed(comment = "This is the host on which process is running")
    public String host;
    
    @JsonExposed(comment = "Port on which this slave may be listening")
    public int port;

    Process(String host, int port, String type, String pid) {
        this.host = host;
        this.port = port;
        this.type = type;
        this.pid = pid;
    }

    public void kill() throws IOException {
        sendPDU(new KillPDU(pid), false);
    }
    
    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

    String sendPDU(PDU pdu, boolean recvBack) throws IOException {
        ESocket sock = new ESocket(getHost(), getPort());
        sock.send(pdu);
        if (recvBack) {
            return sock.readData();
        }
        return "";
    }

}
