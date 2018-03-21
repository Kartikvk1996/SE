#include "master.hpp"

Logger logger;

Master::Master(string configFile) : Probable() {
	
}

void Master::introduce(Socket *sock, PDU &pdu) {
	map<string, Slave*>:: iterator iter;
	for(iter = slaves.begin(); slaves.end() != iter; ++iter) {
		Slave* slv = iter->second;
		vector<Process*> processes = slv->getProcesses();
		for(int i=0; i<processes.size(); ++i){
			processes[i]->sendPDU(new IntroPDU(sock->getHost(), ushort2str(sock->getPort())), false);
		}
	}
}

void Master::handle_connect(Socket *s, PDU &pdu) {
	
	/* If he is a guest then just introduce it to everyone. */
	if(pdu.getSenderType() == PN_GUEST) {
		introduce(s, pdu);
		return;
	}

	json conndata = pdu.getDataAsJson();

	if(pdu.getSenderType() == PN_FIREUP) {
		slaves[s->getHost()] = 
			new Slave(this, s->getHost(), 
				conndata[CONNECT_S_PORT].get<ushort>());
		schedule();
		return;
	}

	/* This is some process we created send back the ACK with PID */
	
	slaves[s->getHost()]->addProcessEntry(
		new Process(
			s->getHost(), 
			conndata[CONNECT_S_PORT].get<ushort>(), 
			pdu.getSenderType()
		)
	);

	PDU p(METHOD_ACK);
	json j;
	j["PID"] = getSlaveCount();
	p.setJData(j);
	s->writeData(p.toString());
}

string Master::toString() {

	int i = 0;
	string s_slaves = "";
	map<string, Slave*>::iterator iter;

	for(iter = slaves.begin(); iter != slaves.end(); ++iter) {
		s_slaves += i++ ? ',' : ' ';
		s_slaves += iter->second->toString();
	}

	return "{\"ip\": \"" + getHost() + "\",\"port\": \"" + ushort2str(getPort()) + "\",\"slaves\": [" + s_slaves + "]}";
}

int Master::getSlaveCount() {
	return slaves.size();
}

void Master::schedule() {

	/* we need some load-balancing kind of algorithm here. 
	 * For the time being we will use a simple algorithm a fixed
	 * set of processes per machine.
	 */

	logger.ilog("trying to schedule jobs");

	map<string, Slave*>:: iterator iter;
	for(iter = slaves.begin(); slaves.end() != iter; ++iter) {
		Slave* slv = iter->second;
		if(slv->getProcessCount() < 2) {
			//slv->createProcess("crawler");
			slv->createProcess("dmgr");
		}
	}
}

void Master::handle_update(Socket *s, PDU &pdu) {
	/* not yet defined */
}

void Master::handle_ack(Socket *s, PDU &pdu) {
	/* not yet defined */
}

/* just leaving for backward compatibility */
void Master::handle_get(Socket *s, PDU &pdu) {

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
		case METHOD_UPDATE:
			handle_update(s, pdu);
			break;
		case METHOD_ACK:
			handle_ack(s, pdu);
			break;
		case METHOD_GET:
			handle_get(s, pdu);
			break;
		default:
			Probable::def_handler(s, pdu);
	}
}

string PROCESS_ROLE = "master";

/*
 * args:
 *	host: IP on which to bind the server. This actually initialises
 *  	   the master-config.
 *	port: port on which the server should be listening.
 */
int main(int argc, char *argv[]) {
	Master master("configFile");
	printf("master running on port  [%d]\n", master.getPort());
	master.run();
}
