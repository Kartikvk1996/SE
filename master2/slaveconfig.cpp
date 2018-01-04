#include "slaveconfig.hpp"

SlaveConfig::SlaveConfig(string ip, string port, string type) {
    this->ip = ip;
    this->port = port;
    this->type = type;
}

string SlaveConfig::toString() {
    return "{\"ip\": \"" + ip + "\",\"port\": " + port + ",\"type\": \"" + type + "\"}";
}