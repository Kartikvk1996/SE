#include <string>
#include <iostream>
#include <bits/stdc++.h>

using namespace std;

#include "../include/debug.h"
#include "../include/util.hpp"
#include "slaveconfig.hpp"

class MasterConfig {

    string ip;
    string port;
    vector<SlaveConfig*> slaves;

public:
    MasterConfig(string ip, string port) {
        this->ip = ip;
        this->port = port;
    }

    void addSlave(SlaveConfig *slave) {
        slaves.push_back(slave);
    }

    string toString() {

        string s_slaves = "";
        
        for(int i = 0; i < slaves.size(); ++i) {
            s_slaves += i ? ',' : ' ';
            s_slaves += slaves[i]->toString();
        }

        return "{\"ip\": \"" + ip + "\",\"port\": \"" + port + "\",\"slaves\": [" + s_slaves + "]}";
    }

    int getSlaveCount() {
        return slaves.size();
    }

    string getHost() {
        return ip;
    }

    string getPort() {
        return port;
    }
};

/*
int main(int argc, char **argv) {
    MasterConfig config("127.0.0.1", 80);
    config.addSlave(new Slave("192.168.2.2", 73));
    config.addSlave(new Slave("192.168.2.3", 74));
    config.addSlave(new Slave("192.168.2.4", 75));

    cout << config.toString() << endl;
}
*/
