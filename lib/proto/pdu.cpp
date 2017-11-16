/*
 * file: [pdu.cpp]
 */

#include "pdu.hpp"

using namespace std;
using json = nlohmann::json;

PDU::PDU(string sender_ip, string sender_port, string receiver_ip, string receiver_port, int method)
{
    setMethod(method);
    senderIP = sender_ip;
    senderPort = sender_port;
    receiverIP = receiver_ip;
    receiverPort = receiver_port;
}

/*Another Constructor initializing sender/receiver module names instead of ip*/
PDU::PDU(string sender, string receiver, int method)
{
    who = sender;
    whom = receiver;
    setMethod(method);
}

string PDU::getMethod()
{
    return method;
}

void PDU::setMethod(int pmethod)
{
    switch (pmethod)
    {
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
void PDU::setData(string bufferedData)
{
    data = bufferedData;
}

json PDU::getJSON()
{
    //	return "{ \"who:\"\"" + who + "\",\n\"whom:\"\"" + whom + "\",\n\"IP:\"\"" + receiverIP + "\",\n\"Port:\"\"" + "8000" + "\",\n\"Data:\"{\"" + data + "\"}\n}";
    json j;
    j["who"] = who;
    j["whom"] = whom;
    j["receiverIP"] = receiverIP;
    j["senderIP"] = senderIP;
    j["receiverPort"] = receiverPort;
    j["senderPort"] = senderPort;
    j["data"] = data;
    j["method"] = method;
    return j;
}

PDU::PDU(string jsonString)
{
    json j = json::parse(jsonString);
    who = j["who"].get<string>();
    whom = j["whom"].get<string>();
    receiverIP = j["receiverIP"].get<string>();
    senderIP = j["senderIP"].get<string>();
    receiverPort = j["receiverPort"].get<int>();
    senderPort = j["senderPort"].get<int>();
    data = j["data"].get<string>();
    method = j["method"].get<string>();
}

string PDU::getSenderIp()
{
    return senderIP;
}
string PDU::getSenderPort()
{
    return senderPort;
}

#if 0
int main()
{
	PDU p("master1", "master2", METHOD_CONNECT);
	p.setData("Hi! Hello World!");
	cout << p.getJSON();
}
#endif