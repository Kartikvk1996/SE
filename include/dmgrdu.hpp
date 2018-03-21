#ifndef DMGRPDU_DEFINED
#define DMGRPDU_DEFINED

#include "dochead.hpp"
#include "consts.hpp"

struct DMgrPDU {

    /* tells whether it's from RGEN or FFC */
    int type;

    union {
        char query[MAX_QUERY_SIZE];
        DocHead dochead;
    };

};

#endif
