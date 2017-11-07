#include <iostream>
#include "../socket.hpp"

using namespace std;

int main(int ar, char **argv) {
	Socket *s = new Socket(string(argv[1]), atoi(argv[2]));
	s->writeData(string("GET / /HTTP 1.0\n\n"));
	cout << s->readData();
}
