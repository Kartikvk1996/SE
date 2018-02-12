#include "master.hpp"

Logger logger;

Master::Master(string host, string port, string configFile) : ReqHandler() {
	this->ip = host;
	this->port = port;
	mserver = new Server(
			getHost(),
			stoi(getPort()),
			this
		);
}

void Master::run() {
	mserver->run();
}

void Master::handle_connect(Socket *s, PDU &pdu) {

	cout << pdu.toString() << endl;

	/* assuming whoever is connecting is fireup. */
	slaves[pdu.getSenderIp()] = new Slave(this, pdu.getSenderIp(), pdu.getSenderPort());

	/* send back the ACK with some random PID */
	PDU p(getHost(), getPort(), 
		pdu.getSenderIp(), pdu.getSenderPort(),
		METHOD_ACK);
	json j;
	j["PID"] = getSlaveCount();
	p.setData(j.dump());
	s->writeData(p.toString());

	if(pdu.getSenderType() == PN_FIREUP)
		schedule();
}

string Master::toString() {

	int i = 0;
	string s_slaves = "";
	map<string, Slave*>::iterator iter;

	for(iter = slaves.begin(); iter != slaves.end(); ++iter) {
		s_slaves += i++ ? ',' : ' ';
		s_slaves += iter->second->toString();
	}

	return "{\"ip\": \"" + getHost() + "\",\"port\": \"" + getPort() + "\",\"slaves\": [" + s_slaves + "]}";
}

int Master::getSlaveCount() {
	return slaves.size();
}

string Master::getHost() {
	return ip;
}

string Master::getPort() {
	return port;
}

void Master::schedule() {

	/* we need some load-balancing kind of algorithm here. 
	 * For the time being we will use a simple algorithm a fixed
	 * set of processes per machine.
	 */

	logger.log("trying to schedule jobs");

	map<string, Slave*>:: iterator iter;
	for(iter = slaves.begin(); slaves.end() != iter; ++iter) {
		Slave* slv = iter->second;
		if(slv->getProcessCount() < 2) {
			slv->createProcess("crawler");
			slv->createProcess("dmgr");
		}
	}
}

void Master::reportStatus(Socket *s) {
	s->writeData("I am fine friend.");
}

void Master::handle_get(Socket *s, PDU &pdu) {
	json jdata = json::parse(pdu.getData());
	string resource = jdata["resource"].get<string>();

	if(resource == "status") {
		reportStatus(s);
	} else if(resource == "config") {
		s->writeData(toString());
	}
}

void Master::handle_update(Socket *s, PDU &pdu) {
	/* not yet defined */
}

void Master::handle_ack(Socket *s, PDU &pdu) {
	/* not yet defined */
}

/* override the ReqHandler method. */
void Master::handle(Socket *s) {
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


/*
 * args:
 *	host: IP on which to bind the server. This actually initialises
 *  	   the master-config.
 *	port: port on which the server should be listening.
 */
int main(int argc, char *argv[]) {

	if(argc < 3) {
		printf("usage : %s hostaddr port\n", argv[0]);
		return 0;
	}

	Master master(argv[1], argv[2], "configFile");
	master.run();
}
