#ifndef _STDC_H
#define _STDC_H
#include<bits/stdc++.h>
using namespace std;
#endif // _STDC

#ifndef _CONFIGURE_H
#include"configure.hpp"
#endif // _CONFIGURE_H

#ifndef _CONNECTION_H
#include"../../common/headers/connection.hpp"
#endif // _CONNECTION_H

#ifndef _QUEUE_H
#include"../../common/headers/queue.hpp"
#endif // _QUEUE_H

#ifndef _LOGGER_H
#define _LOGGER_H
#include"../../common/headers/logger.hpp"
#endif // _LOGGER_H

#ifndef _JSON_H
#define _JSON_H
#include"../../common/headers/json.hpp"
using json=nlohmann::json;
#endif // _JSON_H

#include"../../common/headers/msgcodes.hpp"
#include"master.hpp"
#include"storageengine.hpp"
#include"indexer.hpp"

using Indexer=__INDEXER__::Indexer;
using ServerConnection=__CONNECTION__::ServerConnection;
using ClientConnection=__CONNECTION__::ClientConnection;
using LOG=__LOGGER__::LOG;
using StorageEngine=__STORAGE__::StorageEngine;
using Indexer=__INDEXER__::Indexer;
using Queue=__QUEUE__::Queue;

#define POLLINTERVAL 10

class DataServer
{
private:
    int noOfCrsMasters=0;

    list<Master *> masters;
    mutex lockMasterList;
    mutex poll;

    string DSIpAddress;
    unsigned short DSPort;
    int DSMaxConnection;
    ServerConnection *server=nullptr;
    LOG *log=nullptr;
    mutex queLock;
    unsigned int maxQueueSize=0;
    bool runScheduler=true;

    StorageEngine *se=nullptr;
    Indexer *indx=nullptr;
    bool stop=false;

public:
    Queue *que=nullptr;


    DataServer(string ip,unsigned short pt,int maxConnection)
    {
        DSIpAddress=ip;
        DSPort=pt;
        DSMaxConnection=maxConnection;
        __init();
    }

    ~DataServer()
    {
        if(que!=nullptr)delete que;
        if(server!=nullptr)delete server;
        if(log!=nullptr)delete log;
    }

    void listener();
    void shutdown();
    string statusInfo();


private:
    void __init();
    void requestHandler(CSOCKET);
    json parseRequest(string&);
    void addNewUrls(json &);
    void getUrlsInfo(json &);
    void addCRSMaster(json &,CSOCKET);
    bool removeCRSMaster(string ip , unsigned short int port);
    void pollCRSMasters(short int ,string,unsigned short int );
    void sendData(string& ,unsigned short int &, json& );
    void sendLinksToCRSM(json);
    void Linkscheduler();



};
