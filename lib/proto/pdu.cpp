/*
 * file: [pdu.cpp]
 */

#include "proto/pdu.hpp"

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
    jdata = json::parse(bufferedData);
}

string PDU::getSenderType() {
    return who;
}

json PDU::getJSON()
{
    json j;
    j[WHO] = who;
    j[WHOM] = whom;
    j[RECIEVERIP] = receiverIP;
    j[SENDERIP] = senderIP;
    j[RECEIVERPORT] = receiverPort;
    j[SENDERPORT] = senderPort;
    j[DATA] = jdata;
    j[METHOD] = method;
    return j;
}

PDU::PDU(string &jsonString)
{
    json j = json::parse(jsonString);
    who = j[WHO].get<string>();
    whom = j[WHOM].get<string>();
    receiverIP = j[RECIEVERIP].get<string>();
    senderIP = j[SENDERIP].get<string>();
    receiverPort = j[RECEIVERPORT].get<string>();
    senderPort = j[SENDERPORT].get<string>();
    jdata = j[DATA];
    data = jdata.dump();
    method = j[METHOD].get<string>();
}

string PDU::getSenderIp()
{
    return senderIP;
}
string PDU::getSenderPort()
{
    return senderPort;
}

string PDU::toString() {
    return getJSON().dump();
}

string PDU::getData() {
    return data;
}

json PDU::getDataAsJson() {
    return jdata;
}

#if 0
int main()
{
	PDU p("master1", "master2", METHOD_CONNECT);
	p.setData("Hi! Hello World!");
	cout << p.getJSON();
}
#endif
