#ifndef INTRPDU_INCLUDED
#define INTRPDU_INCLUDED

#include "proto/pdu.hpp"

class IntroPDU : public PDU {
public:
    IntroPDU(string guest_host, string guest_port);
};

#endif