#include <string>
#include <iostream>
#include "../include/util.hpp"

using namespace std;

class SlaveConfig {
    string ip;
    string port;

    /*
     * this is usually set to 'fireup' when fireups connect.
     * but when master2  decides to create processes on these
     * it creates more configs with different names.
     */
    string type;

public:
    SlaveConfig(string ip, string port, string type);
    string toString();
};
