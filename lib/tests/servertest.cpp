#include <iostream>
#include "../server.hpp"

using namespace std;

void handler(Socket *s) {
	cout << s->readData();
	s->writeData(string("Hello World"));
	s->closeConn();
}

int main(int argc, char **argv) {
	Server s("127.0.0.1", atoi(argv[1]), handler);
	s.run();
}
