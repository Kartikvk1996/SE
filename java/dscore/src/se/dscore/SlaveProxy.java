package se.dscore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import se.ipc.Consts;
import se.ipc.ESocket;
import se.ipc.pdu.JsonPathNotExistsException;
import se.ipc.pdu.PDU;
import se.util.Logger;

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
    }

    @Override
    public String toString() {
        return "{\"host\": \"" + host + "}";
    }

    void createProcess(String type) throws IOException {
        PDU pdu = new PDU((String)PDU.METHOD_CREATE);
        try {
            pdu.setValue(Consts.jPath(Consts.DATA, Consts.CMD), "java -jar " + type);
            pdu.setValue(Consts.jPath(Consts.DATA, Consts.ARGS), master.getHost() + " " + master.getPort());
        } catch (JsonPathNotExistsException ex) {
            Logger.ilog(Logger.HIGH, Arrays.toString(ex.getStackTrace()));
        }
        
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
