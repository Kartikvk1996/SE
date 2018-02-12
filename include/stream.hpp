#ifndef STREAM_INCLUDED
#define STREAM_INCLUDED

class Stream {

public:
    virtual int readBytes(void *buffer, int size);
    virtual int writeBytes(void *buffer, int size);

};

#endif