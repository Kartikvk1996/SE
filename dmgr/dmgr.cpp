#include "server.hpp"
#include "proto/pdu.hpp"
#include "proto/errpdu.hpp"
#include "dmgrdu.hpp"
#include "writer.cpp"
#include "logger.hpp"
#include "errors.hpp"
#include "proto/phashes/phashes.hpp"

Logger logger;

class DataManager : public Probable {

    Writer *writer;

public:

    DataManager(string mhost, ushort mport, string wordfile, string docfile):
        Probable() {

        /* report to the master that you are running on port X */
        Socket s(mhost, mport);
        s.writeData(PDU(METHOD_CONNECT).toString());

        writer = new Writer(wordfile, docfile);
    }


    void handle_get(Socket *s, PDU &pdu) {
        PDU *out;
        try {
            string resource = (pdu.getDataAsJson())[GET_RESOURCE];
            if(resource != "writers") {
                out = new ErrorPDU(ERROR_PROTO_RESOURCE, "resource " + resource + " not found");
                logger.elog("resource " + resource + " not found.");
            } else {
                out = new PDU();
                out->setJData(getWritersJSONList());
            }
        } catch(...) {
            logger.elog("Request has no resource field");
            out = new ErrorPDU(ERROR_PROTO_FIELD, "Request has no resource field");
        }
        s->writeData(out->toString());
    }

    json getWritersJSONList() {
        return json();
    }

    /* read the request and process it. refer /docs/ffcdmgrproto.md
     * for the  protocol used here*/
    void handle(Socket *s) {
        string str = s->readData();
        PDU pdu(str);
        const char *cstr = pdu.getMethod().c_str();
        switch(phash(cstr)) {
            case METHOD_GET:
                handle_get(s, pdu);
                break;
            default:
                Probable::def_handler(s, pdu);
        }
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

string PROCESS_ROLE = "dmgr";

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
