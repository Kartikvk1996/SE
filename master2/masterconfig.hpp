#include <string>
#include <iostream>
#include <bits/stdc++.h>
#include "../include/debug.hpp"
#include "../include/util.hpp"
#include "slaveconfig.hpp"

using namespace std;

class MasterConfig {

    string ip;
    string port;
    vector<SlaveConfig*> slaves;

public:
    MasterConfig(string ip, string port);

    void addSlave(SlaveConfig *slave);

    string toString();

    int getSlaveCount();

    string getHost();

    string getPort();
};
