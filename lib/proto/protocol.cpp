/*
 * file: [protocol.hpp]
 * 
 * contributors:
 * 		Anarghya K Jantali	[07 Nov 2017]
 * 		Madhusoodan M Pataki [08 Nov 2017]
 *
 * Declares some method-types, and a wrapper class to build the PDU.
 * 
 */

#include <iostream>
#include <string>
#include "../json.hpp"

/* include function in [phash.hpp] and generated hash values */
#include "phashes.hpp"

using namespace std;
using json = nlohmann::json;
class Protocol
{
private:
	string senderIP;
	string receiverIP;
	short senderPort;
	short receiverPort;
	string method;
	string who, whom; /*This indicates which module is trying to communicate to whom ,I highly advise against initializing this normally
						 Suggested : lookup with ip provided.This also acts as verification.Else I'll leave it upto u guys*/
	string data;

public:
	Protocol(string sender_ip, short sender_port, string receiver_ip, short receiver_port, int method) {
		setMethod(method);
		senderIP = sender_ip;
		senderPort = sender_port;
		receiverIP = receiver_ip;
		receiverPort = receiver_port;
	}

	/*Another Constructor initializing sender/receiver module names instead of ip*/
	Protocol(string sender, string receiver, int method) {
		who = sender;
		whom = receiver;
		setMethod(method);
	}

	void setMethod(int pmethod) {
		switch (pmethod) {
			case METHOD_CONNECT:
				method = "CONNECT";
				break;
			case METHOD_CREATE:
				method = "CREATE";
				break;
			case METHOD_GET:
				method = "GET";
				break;
			case METHOD_UPDATE:
				method = "UPDATE";
				break;
			case METHOD_KILL:
				method = "KILL";
				break;
			case METHOD_WRITE:
				method = "WRITE";
				break;
			case METHOD_ACK:
				method = "ACK";
				break;
			}
	}

	/* separate function to load data/PDU*/
	void setData(string bufferedData) {
		data = bufferedData;
	}

public:
	string getJSON() {
		return "{ \"who:\"\"" + who + "\",\n\"whom:\"\"" + whom + "\",\n\"IP:\"\"" + receiverIP + "\",\n\"Port:\"\"" + "8000" + "\",\n\"Data:\"{\"" + data + "\"}\n}";
	}
};

int main()
{
	Protocol p("master1", "master2", METHOD_CONNECT);
	p.setData("Hi! Hello World!");
	cout << p.getJSON();
}
