package se.dscore;

import java.io.IOException;
import se.ipc.ESocket;
import se.ipc.pdu.PDU;

public class Process {

    public String type;
    public String host;
    public int port;

    Process(String host, int port, String type) {
        this.host = host;
        this.port = port;
        this.type = type;
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
