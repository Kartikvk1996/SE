#include "proto/intropdu.hpp"

IntroPDU::IntroPDU(string guest_host, string guest_port) {
    setMethod(METHOD_INTRO);
    jdata[GUEST_HOST] = guest_host;
    jdata[GUEST_PORT] = guest_port;
}
