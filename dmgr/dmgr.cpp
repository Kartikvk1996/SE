#include "server.hpp"
#include "proto/pdu.hpp"
#include "../lib/util.cpp"
#include "dmgrdu.hpp"
#include "reader.cpp"
#include "writer.cpp"
#include "logger.hpp"
#include "proto/phashes/phashes.hpp"

Logger logger;

class DataManager : public ReqHandler {

    Server *server;
    Reader *reader;
    Writer *writer;

public:

    DataManager(string mhost, ushort mport, string wordfile, string docfile) {

        /*
         * TODO: You really need to check this call.
         * 1. Although it makes sense that server's constructor is
         *   called with hostname as an argument, it's redundant where
         *   the computer connected to a single network interface. btw
         *   we are not handling mulitple n/w interfaces in the Socket.
         * 2. Port number assigned by constructor is not at all good,
         *   they should be set dynamically.
         */
        server = new Server("127.0.0.1", 5002, this);

        /* report to the master that you are running on port X */
        PDU p(server->getHost(), ushort2str(server->getPort()),
            mhost, ushort2str(mport), METHOD_ACK);

        cout << p.toString() << endl;

        Socket s(mhost, mport);
        s.writeData(p.toString());

        writer = new Writer(wordfile, docfile);
    }


    /* read the request and process it. refer /docs/ffcdmgrproto.md
     * for the  protocol used here*/
    void handle(Socket *s) {
        DMgrPDU *pdu = new DMgrPDU();
        if(s->readBytes(pdu, sizeof(DMgrPDU)) > 0) {
            switch(pdu->type) {
                case FFC:
                    writer->writeDoc(&pdu->dochead, s);
                    break;
                case RGEN:
                    reader->replyQuery(pdu->query, s);
            }
        }
    }

    void run() {
        server->run();
    }

    char **split(char *buffer, char delim) {
        int cnt = 1, j = 0;
        for(int i = 0; buffer[i]; ++i) {
            if(buffer[i] == delim)
                cnt++;
        }
        char **list = new char*[cnt + 2];
        list[0] = buffer;
        for(int i = 0; buffer[i]; ++i) {
            if(buffer[i] == delim) {
                list[++j] = &buffer[i+1];
                buffer[i] = 0;
            }
        }
        list[++j] = NULL;
        return list;
    }

};

/*
 * args:
 *  host & port: IP and port pair on which the master is running it's
 *  sent by master only when created via fireup application.
 */
int main(int argc, char *argv[]) {

    if(argc < 3) {
        printf("usage: %s masterip masterport\n", argv[0]);
        return 0;
    }

    DataManager dmgr(argv[1], atoi(argv[2]), "wordfile", "docfile");
    dmgr.run();
}
