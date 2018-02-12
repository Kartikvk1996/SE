#include "stream.hpp"
#include "types.hpp"
#include <string.h>
#include <stdlib.h>

#define MAX_BUFFER_SIZE 1024

class StreamReader {

    char *buffer;
    int pos;
    int size;
    Stream *strm;

public:
    StreamReader(Stream *strm) {
        this->strm = strm;
        pos = 0;
        size = 0;
    }

    /* returns number of bytes read */
    int readLine(char *buf, int msize) {

        int len = msize;

        if(buffer == NULL) {
            buffer = (char*)malloc(MAX_BUFFER_SIZE);
        }

        while(msize) {

            if(size == 0 || pos == size) {
                size = strm->readBytes(buffer, MAX_BUFFER_SIZE);
                pos = 0;
            }

            int cpos = pos;
            for(; --msize && pos < size && buffer[pos] != '\n' && buffer[pos]; ++pos, ++buf)
                *buf = buffer[pos];

            if(pos++ != size)
                break;
        }

        *buf = '\0';
        return (len - msize);
    }

};