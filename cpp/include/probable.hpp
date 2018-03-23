#ifndef PROBABLE_INCLUDED
#define PROBABLE_INCLUDED

#include "node.hpp"
#include "json.hpp"

using json = nlohmann::json;

class Probable : public Node {

public:
    void def_handler(Socket *s, PDU &pdu);
	
private:
    void handle_status(Socket *s, PDU &pdu);
};

#endif