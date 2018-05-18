#include"../headers/include.hpp"


using namespace std;

using ServerConnection=__CONNECTION__::ServerConnection;
using ClientConnection=__CONNECTION__::ClientConnection;
using Queue=__QUEUE__::Queue;
using LOG=__LOGGER__::LOG;



#define POLLINTERVAL 10

class CMS
{

public:
    Queue *que=nullptr;

private:
    LOG *log;
    bool stop=false;
    ServerConnection *server=nullptr;
    ClientConnection *client=nullptr;
    string ipAddress;
    unsigned short int port;
    unsigned short int queueSize;
    unsigned short int maxCrawlers;
    unsigned short int maxConnections;
    unsigned short int currCrawlers;
    string startTime;

    string dsIpAddress;
    unsigned short int dsPort;

public:
    CMS()
    {
        this->currCrawlers=0;
        this->ipAddress=CONFIG::CMS_IPADDRESS;
        this->port=CONFIG::CMS_PORT;
        this->maxCrawlers=CONFIG::CMS_MAXCRAWLERS;
        this->maxConnections=CONFIG::MAXCONNECTION;
        this->queueSize=CONFIG::QUEUE_SIZE;
        getTime(this->startTime);
        __init();
    }

    ~CMS()
    {
        if(que!=nullptr)delete que;
        if(server!=nullptr)delete server;
        if(log!=nullptr)delete log;
    }

private:
    list<Crawler *> crawlerInfo;
    void __init();

public:
//    string getQueueLinks();
//    bool addQueueLinks();
    void connectDataServer();
    void sendPollResponse();
    unsigned int getUrlId();
    bool isUrlPresent();
    void addCrawler(CSOCKET& ,json&);
    void addLinks(json);
    bool removeCrawler(string ,unsigned short int);
    void requestLinks();          /* Requests links from crawler  */
    void sendLinks(CSOCKET,json);   /* Send links to crawlers       */
    void* listenToCrawlers();
    json parseRequest(string &);
    void sendData(int,json&);
    void sendData(string&,unsigned short int&, json&);
    string statusInfo();             /* Sends information in JSON format via HTTP*/
    void serveRequest(int);
    void pollCrawlers(short int,string,unsigned short int);
    void shutdown();
    string systemInfo();
};
