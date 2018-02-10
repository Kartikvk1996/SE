#ifndef LINEREADER_DEFINED
#define LINEREADER_DEFINED

#include <iostream>
using namespace std;

class LineReader {

    char *cur;
public:
    LineReader(string str);
    string nextLine();
};

#endif