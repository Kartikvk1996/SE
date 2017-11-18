#include <string>
#include <iostream>
#include "../include/util.hpp"

using namespace std;

class SlaveConfig {
    string ip;
    string port;

public:
    SlaveConfig(string ip, string port);
    string toString();
};
