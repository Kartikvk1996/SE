using namespace std;

#include "proto/phashes.hpp"
#include "util.hpp"
#include "word.hpp"
#include "dochead.hpp"
#include "socket.hpp"
#include "streamreader.cpp"
#include "probable.hpp"
#include "server.hpp"
#include "dmgrdu.hpp"

#define MAX_OPEN_FILES      (100)
#define INDIRECTION_LEVEL   (1)

/*
 *
 * Associated doc file: writer.md
 * 
 * Please refer the docfile associated with this file before you
 * read this implementation. The parameters for the writers are
 * defined above by which you can tune the performance.
 */

hash_t whash() {

}

class Writer : Probable {

    /* this is used for fast lookup */
    struct open_file {
        fileid_t fid;
        FILE *ptr;
    } *open_files[INDIRECTION_LEVEL][MAX_OPEN_FILES] = { { NULL } };

    string wordfile, docfile;
    FILE *wfile, *dfile;
    Server *server;

public:
    Writer(string wordfile, string docfile) {
        server = new Server(this);

        this->wordfile = wordfile;
        this->docfile = docfile;

        wfile = fopen(wordfile.c_str(), "ab");
        dfile = fopen(docfile.c_str(), "ab");
    }


    /* read the request and process it. refer /docs/ffcdmgrproto.md
     * for the  protocol used here*/
    void handle(Socket *s) {
        DMgrPDU *pdu = new DMgrPDU();
        if(s->readBytes(pdu, sizeof(DMgrPDU)) > 0) {
            switch(pdu->type) {
                case PC_FFC:
                    writeDoc(&pdu->dochead, s);
                    break;
                case PC_RGEN:
                    replyQuery(pdu->query, s);
            }
        }
    }

    /* each word is on a line with it's frequency 
     * extract it then index the from each word */
    void writeDoc(DocHead *dochead, Socket *sock) {

        StreamReader sr(sock);
        char buffer[64];
        char word[64];
        int freq, lvl;
        hash_t hash;
        FILE* curf;

        for(int i=0; i<dochead->dsize; ++i) {

            sr.readLine(buffer, 64);
            sscanf(buffer, "%s%d", word, &freq);

            cstoupper(word);
            
            for(curf = wfile, lvl = 0; lvl < (INDIRECTION_LEVEL - 1); ++lvl) {
                /*
                hash = whash(curf, word) % mods[lvl];
                fseeko64(curf, hash * sizeof(struct ind_blk), SEEK_SET);
                fread(&iblk, sizeof(struct ind_blk), 1, curf);
                curf = open(lvl, )
                */
            }
            /*
            fseeko64(wfile, hash, SEEK_SET);
            wlock();
            fread(&iblk, sizeof(struct ind_blk), 1, wfile);
            wunlock();
            */
            printf("%s    %d\n", word, freq);
        }
    }

    void replyQuery(char *query, Socket *sock) {

    }
    
};