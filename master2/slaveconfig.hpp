#include <string>
#include <iostream>
using namespace std;

#include "../include/util.hpp"

class SlaveConfig {
    string ip;
    string port;

public:
    SlaveConfig(string ip, string port) {
        this->ip = ip;
        this->port = port;
    }

    string toString() {
        return "{\"ip\": \"" + ip + "\",\"port\": " + port + "}";
    }

};
