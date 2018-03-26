package se.dscore;

import java.io.IOException;
import java.util.ArrayList;
import se.ipc.Consts;
import se.ipc.ESocket;
import se.ipc.pdu.CreatePDU;
import se.ipc.pdu.PDU;

class SlaveProxy {

    Master master;
    String host;
    int agentPort;
    ArrayList<Process> processes;

    ArrayList<Process> getProcesses() {
        return processes;
    }

    SlaveProxy(Master master, String host, int agentPort) {
        this.host = host;
        this.agentPort = agentPort;
        this.master = master;
        this.processes = new ArrayList<>();
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
}
