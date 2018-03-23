#include <iostream>
#include "socket.hpp"
#include "proto/pdu.hpp"
#include "logger.hpp"

using namespace std;

/*
 * Protocol test.
 */

Logger logger;

int main(int argc, char **argv) {

	if(argc < 3) {
		printf("usage: %s hostaddr port\n", argv[0]);
		return 0;
	}

	Socket *s = new Socket(string(argv[1]), atoi(argv[2]));
	string str = "buffer";

	for(int i=0; i<3; ++i)
		str += str + "\n";

	cout << "len : " << str.length() << endl;

	s->writeData(str);
	cout << s->readData();
}

