#include "linereader.hpp"

LineReader::LineReader(string str) {
    cur = (char*)str.c_str();
}

string LineReader::nextLine() {
    char tmp, *ret = cur;
    while(*cur && *cur != '\n')
        cur++;
    tmp = *cur;
    *cur = '\0';
    string line = ret;
    *cur = tmp;
    return line;
}