package se.dscore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import jsonparser.JsonExposed;
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
    @JsonExposed public HashMap<Integer, Process> processes;
    @JsonExposed public long heartBeat;

    Master master;

    HashMap<Integer, Process> getProcesses() {
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

    @RESTExposedMethod
    public void kill(int pid) throws IOException {
        sendPDU(new KillPDU(pid), true);
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

    void addProcessEntry(int pid, Process proc) {
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
}
