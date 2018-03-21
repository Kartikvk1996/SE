#include "slave.hpp"

Slave::Slave(Master *master, string host, ushort agentPort) {
    this->host = host;
    this->agentPort = agentPort;
    this->master = master;
}

string Slave::toString() {
    return "{\"host\": \"" + host + "}";
}

int Slave::createProcess(string type) {
    
    PDU p(METHOD_CREATE);
    
    json cmd;
    cmd["CMD"] = type;
    cmd["ARGS"] = master->getHost() + " " + ushort2str(master->getPort());

    p.setData(cmd.dump());

    sendPDU(&p, false);
}

string Slave::getHost() {
    return host;
}

vector<Process*> Slave::getProcesses() {
    return processes;
}

ushort Slave::getAgentPort() {
    return agentPort;
}

int Slave::getProcessCount() {
    return processes.size();
}

void Slave::addProcessEntry(Process* proc) {
    processes.push_back(proc);
}

string Process::sendPDU(PDU *pdu, bool recvBack) {
    Socket *sock = new Socket(getHost(), getPort());
    sock->writeData(pdu->toString());
    if(recvBack) {
        return sock->readData();
    }
    return "";
}

string Slave::sendPDU(PDU *pdu, bool recvBack) {
    Socket *sock = new Socket(getHost(), getAgentPort());
    sock->writeData(pdu->toString());
    if(recvBack) {
        return sock->readData();
    }
    return "";
}
