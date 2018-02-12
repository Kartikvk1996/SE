using namespace std;

#include "word.hpp"
#include "dochead.hpp"
#include "socket.hpp"
#include "streamreader.cpp"

class Writer {

private:
    string wordfile, docfile;

public:
    Writer(string wordfile, string docfile) {
        this->wordfile = wordfile;
        this->docfile = docfile;
    }

    /* each word is on a line with it's frequency 
     * extract it then index the from each word */
    void writeDoc(DocHead *dochead, Socket *sock) {

        StreamReader sr(sock);
        char buffer[64];
        char word[64];
        int freq; 

        for(int i=0; i<dochead->dsize; ++i) {

            sr.readLine(buffer, 64);
            sscanf(buffer, "%s%d", word, &freq);

            printf("%s    %d\n", word, freq);

        }


    }

};