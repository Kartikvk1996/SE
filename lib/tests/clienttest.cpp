#include <iostream>
#include "../socket.hpp"

using namespace std;

int main(int ar, char **argv) {
	Socket *s = new Socket(string(argv[1]), atoi(argv[2]));
	s->writeData(R"({"ip": "127.0.0.1","port": "80","slaves": [ {"ip": "192.168.2.2","port": 73},{"ip": "192.168.2.3","port": 74},{"ip": "192.168.2.4","port": 75}]})");
	cout << s->readData();
}
