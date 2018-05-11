package se.dscore;

import java.io.IOException;
import java.util.Date;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;
import se.ipc.ESocket;
import se.ipc.pdu.CommandPDU;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.PDU;
import se.ipc.pdu.StatusPDU;

public class ProcessProxy {
    
    @JsonExposed(comment = "PID of the process. It's a string")
    public String pid;
    
    @JsonExposed(comment = "This denotes the type of process")
    public String type;
    
    @JsonExposed(comment = "This is the host on which process is running")
    public String host;
    
    @JsonExposed(comment = "Port on which this slave may be listening")
    public int port;

    @JsonExposed(comment = "Last heartbeat interval of the slave")
    public long lastHeartbeatTime;
    
    @JsonExposed(comment = "The error stream of the process")
    public String errFile;
    
    @JsonExposed(comment = "The output stream of the process")
    public String outFile;
    
    @JsonExposed(comment = "This is the http port the process runs if it has any")
    public int httpPort;
    
    ProcessProxy(String host, int port, String type, String pid, String errFile, String outFile, int httpPort) {
        this.host = host;
        this.port = port;
        this.type = type;
        this.pid = pid;
        this.errFile = errFile;
        this.outFile = outFile;
        this.httpPort = httpPort;
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

    public long getLastHBTime() {
        return lastHeartbeatTime;
    }

    void rcvHeartbeat(StatusPDU pdu) {
        lastHeartbeatTime = (new Date()).getTime();
    }

    @RESTExposedMethod(comment = "Runs a method in given process. Nothing is sent back")
    public String runMethod(String method, JsonObject data) {
        try {
            sendPDU(new CommandPDU(method, data), false);
        } catch (InvalidPDUException | IOException ex) {
            return ex.toString();
        }
        return "success";
    }
    
    
}
