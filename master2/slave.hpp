#ifndef SLAVE_INCLUDED
#define SLAVE_INCLUDED

#include <string>
#include <iostream>
#include <bits/stdc++.h>
#include "util.hpp"
#include "master.hpp"

using namespace std;

class Master;
class Process {
    string type;
    string ip;
    string port;

public:
    Process(string ip, string port, string type) {
        this->ip = ip;
        this->port = port;
        this->type = type;
    }
};

class Slave {
    Master *master;
    string ip;
    string port;
    vector<Process*> processes;

public:
    Slave(Master *master, string ip, string port);
    int createProcess(string type);
    string toString();
    string getHost();
    string getPort();
    int getProcessCount();
};

#endif
