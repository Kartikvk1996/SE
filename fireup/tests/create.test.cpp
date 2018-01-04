#include <iostream>
#include "../../lib/socket.hpp"
#include "../../lib/proto/pdu.hpp"

using namespace std;

/*
 * Fireup Create Test.
 *
 * this is test created for testing the fireup module's 'create'
 * functionality. It sends a sample 'create' packet with the 
 * given secret argv[3] to `fireup` running on machine argv[1]
 * on port argv[2].
 */

int main(int argc, char **argv) {

	if(argc < 4) {
		printf("usage: %s hostaddr port secret\n", argv[0]);
		return 0;
	}

	Socket *s = new Socket(string(argv[1]), atoi(argv[2]));

	char buffer[1024];
	sprintf(
		buffer,
		"{'secret': '%s', 'method':'create', 'cmd': 'ls', 'args':'-al1'}",
		argv[3]
	);
	string f = buffer;
	cout << f << endl;
	s->writeData(f + "\n\n");
	cout << s->readData();
}

