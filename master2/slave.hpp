#ifndef SLAVE_INCLUDED
#define SLAVE_INCLUDED

#include <string>
#include <iostream>
#include <bits/stdc++.h>
#include "util.hpp"
#include "master.hpp"
#include "proto/pdu.hpp"

using namespace std;

/**
 * Slave at high level represents a computer node. While there will
 * be many processes. For sake of simplicity we set the port of slave
 * as the port on which fireup is listenting.
 */

class Master;
class Process {
    string type;
    string host;
    ushort port;

public:
    Process(string host, ushort port, string type) {
        this->host = host;
        this->port = port;
        this->type = type;
    }

    string getHost() {
        return host;
    }

    ushort getPort() {
        return port;
    }
    
    string sendPDU(PDU *pdu, bool recvBack);
};

class Slave {
    Master *master;
    string host;
    ushort agentPort;
    vector<Process*> processes;

public:
    vector<Process*> getProcesses();
    Slave(Master *master, string host, ushort port);
    int createProcess(string type);
    string toString();
    string getHost();
    ushort getAgentPort();
    int getProcessCount();
    string sendPDU(PDU *pdu, bool recvBack);
    void addProcessEntry(Process* proc);
};

#endif
