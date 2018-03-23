#include <iostream>
#include "socket.hpp"
#include "proto/pdu.hpp"
#include "logger.hpp"
#include "node.hpp"

using namespace std;

Logger logger;
string PROCESS_ROLE = "guest";
/*
 * Master Intro Test.
 *
 * this code tests the master2 running at argv[1] on port argv[2]
 * It sends a guest packet and checks.
 */

class SampleProber : public Node {
	void handle(Socket *s) {
		cout << s->readData() << endl;
	}
};


void runner(SampleProber *sp) { sp->run(); }

int main(int argc, char **argv) {

	if(argc < 3) {
		printf("usage: %s hostaddr port\n", argv[0]);
		return 0;
	}

	Socket *s = new Socket(string(argv[1]), atoi(argv[2]));

	PDU p(METHOD_CONNECT);
	SampleProber *sp = new SampleProber();
	
	thread(runner, sp).detach();

	json j;
	j[GUEST_S_HOST] = sp->getHost();
	j[GUEST_S_PORT] = ushort2str(sp->getPort());
	p.setJData(j);
	s->writeData(p.toString());
	s->closeConn();
	while(1);
}
