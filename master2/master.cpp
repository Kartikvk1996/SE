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
	
	Master(MasterConfig *config) {
		this->config = config;
		mserver = new Server(config->getHost(), stoi(config->getPort()), handle_request);
	}
	
	void run() {
		mserver->run();
	}
};

MasterConfig *config;

void handle_connect(Socket *s, PDU &pdu) {
	config->addSlave(new SlaveConfig(pdu.getSenderIp(), pdu.getSenderPort()));
	/* send back the ACK */
	PDU p(config->getHost(), config->getPort(), pdu.getSenderIp(), pdu.getSenderPort(), METHOD_ACK);
	json j;
	j["PID"] = config->getSlaveCount();
	p.setData(j.dump());
	s->writeData(p.toString());
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

int main(int argc, char *argv[]) {

	if(argc < 3) {
		printf("usage : %s hostaddr port\n", argv[0]);
		return 0;
	}

	config = new MasterConfig(argv[1], argv[2]);
	Master master(config);
	master.run();
}
