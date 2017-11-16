#include "../lib/json.hpp"
using json = nlohmann::json;

#include <thread>
#include "masterconfig.hpp"
#include "../lib/server.hpp"
#include "../lib/connection.hpp"
#include "../lib/proto/phashes.hpp"
#include "../lib/proto/pdu.hpp"

void handle_request(Socket *s);

class Master
{
public:
	MasterConfig *config;
	Server *mserver;
	
	Master(string host, ushort port) {
		mserver = new Server(host, port, handle_request);
	}
	
	void run() {
		mserver->run();
	}
};

void handle_connect(Socket *s, PDU &pdu) {
	config.addSlave(new SlaveConfig(pdu.getSenderIp(), pdu.getSenderPort()));
	/* send back the ACK */
	PDU p(config.getHost(), config.getPort(), pdu.getSenderIp(), pdu.getSenderPort(), METHOD_ACK);
	json j;
	j["PID"] = config.getSlaveCount();
	p.setData(j);
	s->writeData(p.getJSON());
}

void handle_get(Socket *s, PDU &pdu) {

}

void handle_update(Socket *s, PDU &pdu) {

}

void handle_ack(Socket *s, PDU &pdu) {

}

void handle_request(Socket *s) {
	string str = s->readData();
	PDU pdu(str);
	const char *cstr = pdu.getMethod().c_str();
	switch(phash(cstr)) {
		case METHOD_CONNECT:
			handle_connect(s, pdu);
			break;
		case METHOD_GET:
			handle_get(s, pdu);
			break;
		case METHOD_UPDATE:
			handle_update(s, pdu);
			break;
		case METHOD_ACK:
			handle_ack(s, pdu);
			break;
	}
}

MasterConfig config;

int main(int argc, char *argv[]) {
	Master master(argv[1], atoi(argv[2]));
	master.run();
}
