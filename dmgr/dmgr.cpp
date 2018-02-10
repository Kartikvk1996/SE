#include "server.hpp"
#include "proto/pdu.hpp"
#include "../lib/util.cpp"
#include "linereader.hpp"

class DataManager : public ReqHandler {

    Server *server;

public:

    DataManager(string mhost, ushort mport) {

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

        Socket s(mhost, mport);
        s.writeData(p.toString());

    }




    /* read the request and process it. refer /docs/ffcdmgrproto.md
     * for the  protocol used here*/
    void handle(Socket *s) {
        string data = s->readData();
        LineReader lr(data);

        string type = lr.nextLine();

        /* first line tells which type of request is this. */
        if(type == "RGEN") {
            /* send the query to reader. */
        } else if(type =="FFC") {
            /* send the query to writer. */
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

    DataManager dmgr(argv[1], atoi(argv[2]));
    dmgr.run();
}
