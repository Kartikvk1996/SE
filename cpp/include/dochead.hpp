#ifndef DOCHEAD_INCLUDED
#define DOCHEAD_INCLUDED

#include "types.hpp"

struct DocHead {

    docid_t docid;
    rank_t rank;
    int dsize;      //document size in number of words.

};

#endif