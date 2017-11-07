// ProtocolSkeletal.cpp : Defines the entry point for the console application.
//


#include<iostream>
#include<string>

#define CONNECT 1
#define CREATE 2
#define GET 3
#define UPDATE 4
#define KILL 5
#define WRITE 6
#define ACK 7
using namespace std;

class Protocol {

private:string senderIP;
		string receiverIP;
		short senderPort;
		short receiverPort;
		string method;
		string who, whom;/*This indicates which module is trying to communicate to whom ,I highly advise against initializing this normally
						 Suggested : lookup with ip provided.This also acts as verification.Else I'll leave it upto u guys*/
		string data;
		
public:
	 Protocol(string sender_ip, short sender_port, string receiver_ip, short receiver_port, short protocolMethod)
	{
		switch (protocolMethod)
		{
		case CONNECT:method = "CONNECT"; break;
		case CREATE:method = "CREATE"; break;
		case GET:method = "GET"; break;
		case UPDATE:method = "UPDATE"; break;
		case KILL:method = "KILL"; break;
		case WRITE:method = "WRITE"; break;
		case ACK:method = "ACK"; break;
		default:
			break;
		}
	
		senderIP = sender_ip;
		senderPort = sender_port;
		receiverIP = receiver_ip;
		receiverPort = receiver_port;
	}
	 /*Another Constructor initializing sender/receiver module names instead of ip*/
public:
	Protocol(string sender, string receiver, short protocolMethod) {
		who = sender;
		whom = receiver;
		switch (protocolMethod)
		{
		case CONNECT:method = "CONNECT"; break;
		case CREATE:method = "CREATE"; break;
		case GET:method = "GET"; break;
		case UPDATE:method = "UPDATE"; break;
		case KILL:method = "KILL"; break;
		case WRITE:method = "WRITE"; break;
		case ACK:method = "ACK"; break;
		default:
			break;
		}
	 
	 }
/* separate function to load data/PDU*/
public:
	void setData(string bufferedData) {
		data = bufferedData;
	}
public:
	string getJSON() {
		
		string jsonifiedString = "{ who:\""+who+"\",\nwhom:\""+whom+"\",\nIP:\""+receiverIP+"\",\nPort:\""+"8000"+"\",\nData:{\""+data+"\"}\n}";
		return jsonifiedString;
	}


};
