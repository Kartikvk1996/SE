package se.dscore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsonparser.DictObject;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;
import se.ipc.ESocket;
import se.ipc.pdu.CreatePDU;
import se.ipc.pdu.KillPDU;
import se.ipc.pdu.PDU;
import se.ipc.pdu.StatusPDU;
import se.ipc.pdu.SysInfo;

public class SlaveProxy {

    @JsonExposed public String host;
    @JsonExposed public int agentPort;
    @JsonExposed public SysInfo sysInfo;
    @JsonExposed public HashMap<String, Process> processes;
    @JsonExposed public long heartBeat;

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
        this.heartBeat = new Date().getTime();
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
