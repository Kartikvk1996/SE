#include "masterconfig.hpp"

MasterConfig::MasterConfig(string ip, string port) {
    this->ip = ip;
    this->port = port;
}

void MasterConfig::addSlave(SlaveConfig *slave) {
    slaves.push_back(slave);
}

string MasterConfig::toString() {

    string s_slaves = "";

    for (int i = 0; i < slaves.size(); ++i) {
        s_slaves += i ? ',' : ' ';
        s_slaves += slaves[i]->toString();
    }

    return "{\"ip\": \"" + ip + "\",\"port\": \"" + port + "\",\"slaves\": [" + s_slaves + "]}";
}

int MasterConfig::getSlaveCount() {
    return slaves.size();
}

string MasterConfig::getHost() {
    return ip;
}

string MasterConfig::getPort() {
    return port;
}

/* testing code.
int main(int argc, char **argv) {
    MasterConfig config("127.0.0.1", 80);
    config.addSlave(new Slave("192.168.2.2", 73));
    config.addSlave(new Slave("192.168.2.3", 74));
    config.addSlave(new Slave("192.168.2.4", 75));
    cout << config.toString() << endl;
}
*/
