package se.dscore;

import java.io.IOException;
import java.util.HashMap;
import jsonparser.DictObject;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;
import se.ipc.ESocket;
import se.ipc.pdu.CreatePDU;
import se.ipc.pdu.KillPDU;
import se.ipc.pdu.PDU;
import se.ipc.pdu.StatusPDU;

public class SlaveProxy {

    @JsonExposed(comment = "The host name of the slave")
    public String host;
    
    @JsonExposed(comment = "The port on which the agent is listening")
    public int agentPort;
    
    @JsonExposed(comment = "The slave system's resources")
    public SysInfo sysInfo;
    
    @JsonExposed(comment = "The processes running on this slave")
    public HashMap<String, Process> processes;
    
    Master master;

    HashMap<String, Process> getProcesses() {
        return processes;
    }

    SlaveProxy(Master master, String host, int agentPort, SysInfo sysInfo) {
        this.host = host;
        this.agentPort = agentPort;
        this.master = master;
        this.processes = new HashMap<>();
        this.sysInfo = sysInfo;
    }

    @Override
    public String toString() {
        return "{\"host\": \"" + host + "}";
    }

    public void kill(String pid) throws IOException {
        sendPDU(new KillPDU(pid), false);
        processes.remove(pid);
    }
    
    void createProcess(String type) throws IOException {
        PDU pdu = new CreatePDU(
                type,
                master.getHost() + " " + master.getPort()
        );
        sendPDU(pdu, false);
    }

    String getHost() {
        return host;
    }

    int getAgentPort() {
        return agentPort;
    }

    int getProcessCount() {
        return processes.size();
    }

    void addProcessEntry(String pid, Process proc) {
        processes.put(pid, proc);
    }

    String sendPDU(PDU pdu, boolean recvBack) throws IOException {
        ESocket sock = new ESocket(getHost(), getAgentPort());
        sock.send(pdu);
        if (recvBack) {
            return sock.readData();
        }
        return "";
    }

    void rcvHeartBeat(StatusPDU pdu) {
        
    }
    
    @RESTExposedMethod
    public String kill(JsonObject data) {
        try {
            kill((String) ((DictObject)data).get("pid").getValue());
        } catch (IOException ex) {
            return "failed";
        }
        return "success";
    }
}
