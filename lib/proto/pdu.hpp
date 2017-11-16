/*
 * file: [pdu.hpp]
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
#include "phashes.hpp"

using namespace std;
using json = nlohmann::json;
class PDU
{
private:
	string senderIP;
	string receiverIP;
	string senderPort;
	string receiverPort;
	string method;
	string who, whom; /*This indicates which module is trying to communicate to whom ,I highly advise against initializing this normally
						 Suggested : lookup with ip provided.This also acts as verification.Else I'll leave it upto u guys*/
	string data;

public:
	PDU(string sender_ip, string sender_port, string receiver_ip, string receiver_port, int method);

	/*Another Constructor initializing sender/receiver module names instead of ip*/
	PDU(string sender, string receiver, int method);
	
	PDU(string jsonString);

	string getMethod();

	void setMethod(int pmethod);

	/* separate function to load data/PDU*/
	void setData(string bufferedData);

	json getJSON();

	string getSenderIp();

	string getSenderPort();

};