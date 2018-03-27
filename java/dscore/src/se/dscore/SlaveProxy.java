package se.dscore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import se.ipc.ESocket;
import se.ipc.pdu.CreatePDU;
import se.ipc.pdu.PDU;
import se.ipc.pdu.StatusPDU;
import se.ipc.pdu.SysInfo;

public class SlaveProxy {

    public String host;
    public int agentPort;
    public SysInfo sysInfo;
    public ArrayList<Process> processes;
    public long heartBeat;

    Master master;

    ArrayList<Process> getProcesses() {
        return processes;
    }

    SlaveProxy(Master master, String host, int agentPort, SysInfo sysInfo) {
        this.host = host;
        this.agentPort = agentPort;
        this.master = master;
        this.processes = new ArrayList<>();
        this.sysInfo = sysInfo;
    }

    @Override
    public String toString() {
        return "{\"host\": \"" + host + "}";
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

    void addProcessEntry(Process proc) {
        processes.add(proc);
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
