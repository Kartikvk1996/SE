#include "slaveconfig.hpp"

SlaveConfig::SlaveConfig(string ip, string port) {
    this->ip = ip;
    this->port = port;
}

string SlaveConfig::toString() {
    return "{\"ip\": \"" + ip + "\",\"port\": " + port + "}";
}