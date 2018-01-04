#include <iostream>
#include "../../lib/socket.hpp"
#include "../../lib/proto/pdu.hpp"

using namespace std;

/*
 * Master Connect Test.
 *
 * this code tests the master2 running at argv[1] on port argv[2]
 * It sends a connect packet to the master2 and shows the pid 
 * recieved.
 */

int main(int argc, char **argv) {

	if(argc < 3) {
		printf("usage: %s hostaddr port\n", argv[0]);
		return 0;
	}

	Socket *s = new Socket(string(argv[1]), atoi(argv[2]));

	PDU p("localhost", "100", argv[1], argv[2], METHOD_CONNECT);

	s->writeData(p.toString());
	cout << s->readData();
}

