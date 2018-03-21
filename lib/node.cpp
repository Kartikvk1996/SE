#include "node.hpp"

Node::Node() {
    server = new Server(this);
}

void Node::run() {
    server->run();
}

string Node::getHost() {
    return server->getHost();
}

ushort Node::getPort() {
    return server->getPort();
}

string Node::toString() {
    string str = "";
    return str + "{\"host\": " + getHost() + ", \"port\": " + ushort2str(getPort()) + "}";
}

void Node::handle_intro(Socket *s, PDU &pdu) {
    
    json data = pdu.getDataAsJson();
    string guesthost = data[GUEST_HOST].get<string>();
    string guestport = data[GUEST_PORT].get<string>();
    IntroPDU intro(guesthost, guestport);

    s->writeData(intro.toString());
}

void Node::handle(Socket *s) {

}

void Node::def_handler(Socket *s, PDU &pdu) {
    char *cstr = (char *)pdu.getMethod().c_str();
    switch(phash(cstr)) {
        case METHOD_INTRO:
            handle_intro(s, pdu);
            break;
    }
}
