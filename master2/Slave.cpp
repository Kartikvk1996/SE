#include <string>
#include <iostream>
using namespace std;

#include "../include/util.hpp"

class Slave {
    string ip;
    string port;

public:
    Slave(string ip, ushort port) {
        this->ip = ip;
        this->port = ushort2str(port);
    }

    string toString() {
        return "{\"ip\": \"" + ip + "\",\"port\": " + port + "}";
    }

};
