#include "MasterConfig.cpp"
#include "../lib/connection.hpp"
#include <thread>

class Master {

public:
	MasterConfig config;
        
    Master() {
        (new Thread(run)).join();
    }


    void run() {
        ServerConnection conn("localhost", 0, 10);
        while(conn.)
    }



};
