package se.dscore;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import jsonparser.DictObject;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;
import se.ipc.ESocket;
import se.ipc.pdu.CreatePDU;
import se.ipc.pdu.KillPDU;
import se.ipc.pdu.PDU;
import se.ipc.pdu.StatusPDU;

public class NodeProxy {

    @JsonExposed(comment = "The host name of the slave")
    public String host;

    @JsonExposed(comment = "The port on which the agent is listening")
    public int agentPort;

    @JsonExposed(comment = "This is the port on which logs are served")
    public int logPort;

    @JsonExposed(comment = "The slave system's resources")
    public SysInfo sysInfo;

    @JsonExposed(comment = "This is the last heartbeat time")
    public long lastHeartbeat;

    @JsonExposed(comment = "The processes running on this slave")
    public HashMap<String, ProcessProxy> processes;

    MasterProcess master;

    public HashMap<String, ProcessProxy> getProcesses() {
        return processes;
    }

    public NodeProxy(MasterProcess master, String host, int agentPort, int logPort, SysInfo sysInfo) {
        this.host = host;
        this.agentPort = agentPort;
        this.master = master;
        this.processes = new HashMap<>();
        this.logPort = logPort;
        this.sysInfo = sysInfo;
    }

    @Override
    public String toString() {
        return "{\"host\": \"" + host + "}";
    }

    public void kill(String pid) throws IOException {
        sendPDU(new KillPDU(pid), false);
    }

    public void createProcess(String executableName, String argString) throws IOException {
        PDU pdu = new CreatePDU(
            executableName,
            argString
        );
        sendPDU(pdu, false);
    }

    public String getHost() {
        return host;
    }

    public int getAgentPort() {
        return agentPort;
    }

    public int getProcessCount() {
        return processes.size();
    }

    public void addProcessEntry(String pid, ProcessProxy proc) {
        processes.put(pid, proc);
    }

    public String sendPDU(PDU pdu, boolean recvBack) throws IOException {
        ESocket sock = new ESocket(getHost(), getAgentPort());
        sock.send(pdu);
        if (recvBack) {
            return sock.readData();
        }
        return "";
    }

    void rcvHeartBeat(StatusPDU pdu) {
        this.lastHeartbeat = (new Date()).getTime();
    }

    @RESTExposedMethod(comment = "Kills a process on this slave whose PID is sent")
    public String kill(JsonObject data) {
        try {
            kill((String) ((DictObject) data).get("pid").getValue());
        } catch (IOException ex) {
            return "failed";
        }
        return "success";
    }
}
