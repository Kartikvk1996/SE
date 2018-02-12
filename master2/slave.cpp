#include "slave.hpp"

Slave::Slave(Master *master, string ip, string port) {
    this->ip = ip;
    this->port = port;
    this->master = master;
    processes.push_back(new Process(ip, port, "fireup"));
}

string Slave::toString() {
    return "{\"ip\": \"" + ip + "}";
}

int Slave::createProcess(string type) {
    
    PDU p(getHost(), getPort(),
        master->getHost(), master->getPort(), METHOD_CREATE);
    
    json cmd;
    cmd["CMD"] = type;
    cmd["ARGS"] = master->getHost() + " " + master->getPort();

    p.setData(cmd.dump());

    Socket sock(getHost(), stoi(getPort()));
    sock.writeData(p.toString());

    string res = sock.readData();

}

string Slave::getHost() {
    return ip;
}

string Slave::getPort() {
    return port;
}

int Slave::getProcessCount() {
    return processes.size();
}
