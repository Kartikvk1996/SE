#include <iostream>
#include "socket.hpp"
#include "proto/pdu.hpp"
#include "dmgrdu.hpp"
#include "proto/phashes.hpp"
#include "logger.hpp"

using namespace std;

Logger logger;

/*
 * Data Recieve test. Sends a sample packet to the DMGR
 * and tests it.
 */

int main(int argc, char **argv) {

	if(argc < 3) {
		printf("usage: %s hostaddr port\n", argv[0]);
		return 0;
	}

	Socket *s = new Socket(string(argv[1]), atoi(argv[2]));
	
	char buffer[1024];
	char *wordlist = "\
I 1\n\
am 2\n\
unable 3\n\
to 4\n\
form 2\n\
any 3\n\
kind 5\n\
of 4\n\
sentences 9\n\
Bye 3\n";

	DMgrPDU *dpdu = (DMgrPDU*)buffer;
	dpdu->type = PC_FFC;
	dpdu->dochead.docid = 12345;
	dpdu->dochead.rank = 98765;
	dpdu->dochead.dsize = 10;

	strcpy(((char*)buffer) + sizeof(DMgrPDU), wordlist);
	s->writeBytes(buffer, sizeof(DMgrPDU) + strlen(wordlist));

	return 0;
}

