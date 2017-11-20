#include <iostream>
#include "../socket.hpp"
#include "../proto/pdu.hpp"

using namespace std;

int main(int argc, char **argv) {

	if(argc < 3) {
		printf("usage: %s hostaddr port\n", argv[0]);
		return 0;
	}

	Socket *s = new Socket(string(argv[1]), atoi(argv[2]));

	PDU p(argv[1], "100", argv[1], argv[2], METHOD_CONNECT);

	s->writeData(p.toString());
	cout << s->readData();
}
