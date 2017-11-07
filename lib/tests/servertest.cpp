#include <iostream>
#include "../socket.hpp"

using namespace std;

int main(int ar, char **argv) {
	ServerSocket *x = new ServerSocket(string("127.0.0.1"), atoi(argv[1]), 10);
	Socket *s = x->acceptConn();
	cout << s->readData();
	s->writeData("Hello from server!\n");
}
