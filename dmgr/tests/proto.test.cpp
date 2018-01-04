#include <iostream>
#include "../../lib/socket.hpp"
#include "../../lib/proto/pdu.hpp"

using namespace std;

/*
 * Master Config Test.
 *
 * this code tests the master2 running at argv[1] on port argv[2]
 * It sends a status packet to the master2 and shows the status
 * of the server sent as it.
 */

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

