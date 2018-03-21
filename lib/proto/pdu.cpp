/*
 * file: [pdu.cpp]
 */

#include "proto/pdu.hpp"

void PDU::setWho(string pname) {
    who = pname;
    for(auto &c : who) c = toupper(c);
}

PDU::PDU(int method) {
    setMethod(method);
    setWho(PROCESS_ROLE);
}

PDU::PDU() {
    setWho(PROCESS_ROLE);
}

void PDU::setJData(json jobj) {
	jdata = jobj;
}

string PDU::getMethod() {
    return method;
}

void PDU::setMethod(int pmethod) {
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
    case METHOD_STATUS:
        method = "STATUS";
        break;
    case METHOD_INTRO:
        method = "INTRO";
        break;
    case METHOD_ERROR:
        method = "ERROR";
        break;
    }
}

/* separate function to load data/PDU*/
void PDU::setData(string bufferedData) {
    jdata = json::parse(bufferedData);
}

string PDU::getSenderType() {
    return who;
}

json PDU::getJSON() {
    json j;
    j[WHO] = who;
    j[DATA] = jdata;
    j[METHOD] = method;
    return j;
}

PDU::PDU(string &jsonString) {
    json j = json::parse(jsonString);
    jdata = j[DATA];
    data = jdata.dump();
    method = j[METHOD].get<string>();
    setWho(j[WHO].get<string>());
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
