#ifndef MASTER2_INCLUDED
#define MASTER2_INCLUDED

#include "json.hpp"
using json = nlohmann::json;

#include <thread>
#include "proto/phashes.hpp"
#include "slave.hpp"
#include "reqhandler.hpp"
#include "logger.hpp"
#include "pnames.hpp"
#include "probable.hpp"
#include "proto/intropdu.hpp"

class Slave;

class Master : public Probable 
{
	map<string, Slave*> slaves;
	
public:

	Master(string configFile);
	
	void handle_connect(Socket *s, PDU &pdu);

	string toString();

	int getSlaveCount();

	void handle_get(Socket *s, PDU &pdu);

	void handle_update(Socket *s, PDU &pdu);

	void handle_ack(Socket *s, PDU &pdu);

	/* override the ReqHandler method. */
	void handle(Socket *s);

private:
	void schedule();	
	void introduce(Socket *sock, PDU &pdu);
};

#endif