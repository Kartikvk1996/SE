#ifndef MASTER2_INCLUDED
#define MASTER2_INCLUDED

#include "json.hpp"
using json = nlohmann::json;

#include <thread>
#include "server.hpp"
#include "proto/phashes.hpp"
#include "proto/pdu.hpp"
#include "slave.hpp"
#include "reqhandler.hpp"
#include "logger.hpp"
#include "pnames.hpp"

class Slave;

class Master : public ReqHandler
{
	string ip;
    string port;
	map<string, Slave*> slaves;
	Server *mserver;
	
public:

	Master(string host, string port, string configFile);
	
	void run();

	void handle_connect(Socket *s, PDU &pdu);

	string toString();

	int getSlaveCount();

	string getHost();

	string getPort();

	void schedule();

	void reportStatus(Socket *s);

	void handle_get(Socket *s, PDU &pdu);

	void handle_update(Socket *s, PDU &pdu);

	void handle_ack(Socket *s, PDU &pdu);

	/* override the ReqHandler method. */
	void handle(Socket *s);

};

#endif