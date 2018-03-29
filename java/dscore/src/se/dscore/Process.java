package se.dscore;

import java.io.IOException;
import jsonparser.JsonExposed;
import se.ipc.ESocket;
import se.ipc.pdu.KillPDU;
import se.ipc.pdu.PDU;

public class Process {

    @JsonExposed public String pid;
    @JsonExposed public String type;
    @JsonExposed public String host;
    @JsonExposed public int port;

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
