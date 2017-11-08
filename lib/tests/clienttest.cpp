#include <iostream>
#include "../socket.hpp"

using namespace std;

int main(int ar, char **argv) {
	Socket *s = new Socket(string(argv[1]), atoi(argv[2]));
	s->writeData(R"({"method": "GET"}
)");
	cout << s->readData();
}
