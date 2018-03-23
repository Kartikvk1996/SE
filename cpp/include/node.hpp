#include "reqhandler.hpp"
#include "proto/intropdu.hpp"
#include "proto/phashes.hpp"
#include "util.hpp"
#include "server.hpp"

using namespace std;

class Node : public ReqHandler {

    Server *server;

public:

    Node();
    virtual void run();
    string getHost();
    ushort getPort();

    void def_handler(Socket *s, PDU &pdu);
	virtual string toString();
    virtual void handle(Socket *s);

private:
    void handle_intro(Socket *s, PDU &pdu);
    
};
