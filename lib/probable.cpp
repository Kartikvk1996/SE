#include "probable.hpp"

void Probable::def_handler(Socket *s, PDU &pdu) {
    const char *cstr = pdu.getMethod().c_str();
    switch(phash(cstr)) {
        case METHOD_STATUS:
            handle_status(s, pdu);
            break;
        default:
            Node::def_handler(s, pdu);
    }
}

void Probable::handle_status(Socket *s, PDU &pdu) {
    s->writeData("TODO: Needs to be implemented");
}
