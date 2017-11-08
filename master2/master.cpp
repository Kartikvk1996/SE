#include <thread>

#include "MasterConfig.cpp"
#include "../lib/server.hpp"
#include "../lib/connection.hpp"
#include "../lib/json.hpp"

using json=nlohmann::json;

void handle_request(Socket *s);

class Master {

public:
    MasterConfig *config;
    Server *mserver;
        
    Master(string host, ushort port) {
        mserver = new Server(host, port, handle_request);
    }

    void run() {
        mserver->run();
    }
};

void handle_request(Socket *s) {
    string str = s->readData();
    json req = json::parse(str);
    cout << req.dump();
}

int main(int argc, char *argv[]) {
    Master master(argv[1], atoi(argv[2]));
}
