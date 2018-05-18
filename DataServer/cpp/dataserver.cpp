#include"../headers/dataserver.hpp"


#define LNKSHDSLEEPINT 180

void DataServer::__init()
{
    log=new LOG(CONFIG::LOG_FILENAME,CONFIG::LOG_CREATEDATEWISE,CONFIG::LOG_TOCONSOLE,CONFIG::LOG_CNSCOLOR);
    log->write(LOG::INFO,"[%s]\tStarting the DataServer",__func__);
    try{

        log->write(LOG::INFO,"[%s]\tStarting internal server on IP %s:%d\t",__func__,DSIpAddress.c_str(),DSPort);
        server=new ServerConnection(DSIpAddress,DSPort,DSMaxConnection);
        log->write(LOG::INFO,"[%s]\tInternal server on IP %s:%d started\t",__func__,DSIpAddress.c_str(),DSPort);

        log->write(LOG::INFO,"[%s]\tHTTP server on IP %s:%d started\t",__func__,CONFIG::HTTPSERVER_IPADDRESS.c_str(),CONFIG::HTTPSERVER_PORT);


        log->write(LOG::INFO,"[%s]\tStarting Indexer",__func__);
        indx = new Indexer(CONFIG::INDEXER_DATAFILE);

        log->write(LOG::INFO,"[%s]\tStarting StorageEngine",__func__);
        se=new StorageEngine(CONFIG::STORAGEENGINE_SPLITFACTOR,CONFIG::STORAGEENGINE_CACHESIZE);
        log->write(LOG::INFO,"[%s]\tStorageEngine started successfully",__func__);

        que=new Queue(CONFIG::QUEUE_SIZE);
        maxQueueSize=CONFIG::QUEUE_SIZE;
        log->write(LOG::INFO,"[%s]\tQueue instance created",__func__);

        log->write(LOG::INFO,"[%s]\tQueue loading previous saved instance ",__func__);
        unsigned long int nlinks=que->readInstance();
        log->write(LOG::INFO,"[%s]\tQueue previous saved instance loaded with %u links ",__func__,nlinks);

        log->write(LOG::INFO,"[%s]\tDataServed Started",__func__,nlinks);


        // start scheduler
        std::thread lnksched(&DataServer::Linkscheduler,this);
        lnksched.detach();
        log->write(LOG::INFO,"[%s]\tLink Scheduler started",__func__,nlinks);





    }
    catch(__CONNECTION__::SocketException &e)
    {
        log->write(LOG::ERROR,"[%s]\tException : %s",__func__,e.what());
        raise(SIGABRT);
    }
    catch(std::exception &e)
    {
        log->write(LOG::ERROR,"[%s]\tException : %s",__func__,e.what());
        raise(SIGABRT);
    }
    log->write(LOG::INFO,"[%s]\tDataServer started",__func__);
    return;
}


void DataServer::listener()
{
    while(!stop)
    {
        try{
            CSOCKET fd=server->acceptConnection();
            std::thread worker(&DataServer::requestHandler,this,fd);
            worker.join();
        }
        catch(__CONNECTION__::SocketException &e)
        {
            if(errno!=ECONNABORTED)
            {
                log->write(LOG::WARNING,"[%s]\tException : %s",__func__,e.what());
            }
        }
    }
}


void DataServer::requestHandler(CSOCKET fd)
{
    json pData;
    string inpData;

    try
    {
        inpData=server->readData(fd);
        cout<<inpData<<"\n";
        pData=DataServer::parseRequest(inpData);
        int reqType;

        if(inpData!="NULL")
        {
            reqType=pData["type"];
            switch(reqType)
            {
                case NEW_URLS : {
                                    addNewUrls(pData);
                                    break;
                                }

                case CRSMASTER_CONNECT : {

                                            addCRSMaster(pData,fd);
                                            break;
                                        }

                case GET_URLS : {
                                    getUrlsInfo(pData);
                                    break;
                                }

                case CMS_REQ_NLINKS : {
                                        sendLinksToCRSM(pData);
                                        break;
                                        }

                case CRSMASTER_POLL_ALIVE:  {
                                                string ip=pData["ipaddress"];
                                                unsigned int port=pData["port"];
                                                DataServer::pollCRSMasters(1,ip,port);
                                                break;
                                            }


            }

        }
    }
    catch(std::exception &e)
    {
        log->write(LOG::CRITICAL,"[%s]\tException : %s",__func__,e.what());
    }
    close(fd);
    return;
}

json DataServer::parseRequest(string& data)
{
    json j;
    try
    {
        j=json::parse(data);
    }
    catch(std::exception &e)
    {
        log->write(LOG::WARNING,"[%s]\tException : %s\n%s",__func__,e.what(),data.c_str());
    }
    return j;
}

void DataServer::addNewUrls(json &pData)
{
    int cnt=0;
    try
    {
        vector<string> urlList=pData["urls"];
        log->write(LOG::INFO,"[%s]\tAdding new url links",__func__);
        for(auto ittr=urlList.begin();ittr!=urlList.end();ittr++)
        {
            string url=*ittr;

            if(url.length()<URLLENGTH)
            {
                if(1)
                {

                    unsigned long int urlId=se->addUrl(indx->generateChecksum(url), url);
                    indx->indexUrl(url,urlId);
                    cnt++;
                }
            }
            else
            {
                log->write(LOG::CRITICAL,"[%s]\tURL length overflow  URL length : %d\tMax length : %d",__func__,url.length(),URLLENGTH);
            }

        }
    }
    catch(__INDEXER__::IndexException &e)
    {
        throw __INDEXER__::IndexException(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }
    catch(__STORAGE__::StorageException &e)
    {
        throw __STORAGE__::StorageException(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }
    catch(std::exception &e)
    {
        throw runtime_error(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }
    log->write(LOG::INFO,"[%s]\t%d new url links added successfully",__func__,cnt);
    return;
}

void DataServer::getUrlsInfo(json &pData)
{
    string ip=pData["ipaddress"];
    unsigned int port=pData["port"];
    json sData;
    try
    {
        log->write(LOG::INFO,"[%s]\tFetching url addresses",__func__);
        vector<unsigned long int> urlIds=pData["urlIds"];
        json urlInfo=se->getUrlsInfo(urlIds);
        sData["type"]=URL_RESULTS;
        sData["results"]=urlInfo;
    }
    catch(__STORAGE__::StorageException &e)
    {
        throw __STORAGE__::StorageException(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }
    catch(std::exception &e)
    {
        throw runtime_error(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }

    ClientConnection c(ip,port);
    c.writeData(sData.dump(2));
    return;
}

void DataServer::addCRSMaster(json &pData,CSOCKET fd)
{
    json sData;
    sData["mode"]=RESPONSE;

    std::lock_guard<std::mutex> lock(lockMasterList);
    bool duplicate=false;
    try
    {
        log->write(LOG::INFO,"[%s]\tNew CRSMaster registering",__func__);

        Master *m=new Master();
        m->mIpAddress=pData["ipaddress"];
        m->mHttpAddress=pData["httpipaddress"];
        m->mPort=pData["port"];
        m->mHttpPort=pData["httpport"];
        m->poll=0;
        m->rTime=std::time(nullptr);
        m->crawlers=pData["crawlers"];

        for(auto ittr=masters.begin();ittr!=masters.end();ittr++)
        {
            if((*ittr)->mIpAddress == m->mIpAddress && (*ittr)->mPort == m->mPort)
            {
                duplicate=true;
                delete m;
                break;
            }
        }

        if(duplicate==false)
        {
            noOfCrsMasters++;
            sData["type"]=DS_CONNECT_SUCC;
            masters.push_back(m);

            if(noOfCrsMasters==1)
            {
                std::thread pollThread(&DataServer::pollCRSMasters,this,0,"",0);
                pollThread.detach();
                log->write(LOG::INFO,"[%s]\tpolling stated on CRSMasters ",__func__);
            }
            log->write(LOG::INFO,"[%s]\tNew CRSMaster added successfully IP : %s\tPORT : %hu ",__func__,m->mIpAddress.c_str(),m->mPort);

            server->writeData(fd,sData.dump(1));
        }
    }
    catch(std::exception &e)
    {
        throw runtime_error(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }
}


void DataServer::pollCRSMasters(short int result=0,string ip="",unsigned short int port=0)
{
    // if result is 1 then there is a response from CRS (Crawler)
    if(result==0)
    {
        json pollData;
        pollData["mode"]=REQUEST;
        pollData["type"]=DS_POLL;

        while(true)
        {

            poll.lock();  // enter critical section

            if(noOfCrsMasters<1)
            {
                poll.unlock();
                log->write(LOG::INFO,"[%s]\tpolling stopped , No CRSMasters attached",__func__);
                break;
            }


            for(auto crm=masters.begin();crm!=masters.end();crm++)
            {
                if((*crm)->poll<3)  // check for 3 attempts
                {
                    (*crm)->poll=(*crm)->poll+1;
                    try
                    {
                        sendData((*crm)->mIpAddress,(*crm)->mPort,pollData);
                        //CMS::removeCrawler((*cr)->crawlerIpAddress,(*cr)->crawlerPort);
                    }
                    catch(std::exception &e)
                    {
                        log->write(LOG::WARNING,"[%s]\tFailed to send poll message to CRSMaster %s:%p\nException : %s",__func__,(*crm)->mIpAddress.c_str(),(*crm)->mPort,e.what());
                    }
                }
                else
                {
                    if(DataServer::removeCRSMaster((*crm)->mIpAddress,(*crm)->mPort))
                    {
                        log->write(LOG::INFO,"[%s]\tCRSMaster with IP : %s\tPORT : %hu\tremoved successfully",__func__,(*crm)->mIpAddress.c_str(),(*crm)->mPort);
                    }
                    else
                    {
                        log->write(LOG::WARNING,"[%s]\tFailed to remove Crawler with IP : %s\tPORT : %hu , No information found.",__func__,(*crm)->mIpAddress.c_str(),(*crm)->mPort);
                    }
                    break;
                }

            }
            poll.unlock();
            std::this_thread::sleep_for(chrono::seconds(POLLINTERVAL));

        }
    }
    else
    {
        poll.lock();
        for(auto crm=masters.begin();crm!=masters.end();crm++)
        {
            if(((*crm)->mIpAddress==ip) && ((*crm)->mPort==port))
            {
                (*crm)->poll=0;
                break;
            }
        }
        poll.unlock();
        return;
    }

}

bool DataServer::removeCRSMaster(string ip , unsigned short int port)
{
    // block other threads
    std::lock_guard<std::mutex> lock(lockMasterList);

    json response;
    // search for the CRS (crawler)
    for(auto crm=masters.begin();crm!=masters.end();crm++)
    {
        if(((*crm)->mIpAddress==ip) && ((*crm)->mPort==port))
        {
            delete *crm;  // delete the crawler
            masters.erase(crm);  // remove it from the list
            noOfCrsMasters--;
            return true ;
        }
    }
    return false;
}


void DataServer::sendData(string& ip,unsigned short int &port, json& Data)
{
    cout<<Data.dump(1)<<"\n";
    try
    {
        ClientConnection c(ip,port);
        c.writeData(Data.dump());
    }
    catch(__CONNECTION__::SocketException &e)
    {
        log->write(LOG::WARNING,"[%s]\tException : %s Errorno %d",__func__,e.what(),errno);
    }
    catch(std::runtime_error &e)
    {
        log->write(LOG::CRITICAL,"[%s]\tException : %s ",__func__,e.what());
        throw e;
    }
}


void DataServer::sendLinksToCRSM(json pData)
{

    string ipAddress=pData["ipaddress"];
    unsigned short int port=pData["port"];
    int noOfLinks=pData["nooflinks"];
    log->write(LOG::INFO,"[%s]\tSending %d links to CRSMaster : %s:%d",__func__,noOfLinks,ipAddress.c_str(),port);

        queLock.lock();
        json sData=que->getLinks(noOfLinks);
        queLock.unlock();

    sendData(ipAddress,port,sData);
    int cnt=sData["nooflinks"];
    log->write(LOG::INFO,"[%s]\t%d links to sent CRSMaster : %s:%d",__func__,cnt,ipAddress.c_str(),port);
    return;
}


void DataServer::Linkscheduler()
{
    try
    {
        json j,urlInfo;
        struct linkInfo *link;
        unsigned long int urlId=1;
        log->write(LOG::INFO,"[%s]\tScheduler started",__func__);
        while(runScheduler==true)
        {
            int cnt=0;
            if(que->getNoOfLinks()<maxQueueSize)
            {
                for(int i=0;i<maxQueueSize-que->getNoOfLinks();i++)
                {
                    if(urlId>=se->getTotalLinks())
                        urlId=1;


                    link=se->getUrlToCrawl(urlId);
                    if(link!=NULL)
                    {
                        urlInfo["urlid"]=link->urlId;
                        urlInfo["url"]=link->url;
                        urlInfo["checksum"]=link->checksum;
                        urlInfo["pgrank"]=link->pgrank;
                        urlInfo["pagesize"]=link->pageSize;
                        urlInfo["ilinks"]=link->iLinks;
                        urlInfo["olinks"]=link->oLinks;
                        j["data"][cnt++]=urlInfo;
                        urlId++;
                    }

                }
                j["nooflinks"]=cnt;
                queLock.lock();
                short status=que->addLinks(j);
                if(status==LESS_QSPACE)
                    log->write(LOG::CRITICAL,"[%s]\tNo Space in queue to add new links to recrawl",__func__);
                else
                    log->write(LOG::INFO,"[%s]\t%d links were scheduled for recrawl",__func__,status);

                queLock.unlock();
                j["data"].clear();

            }
            else
            {
                log->write(LOG::INFO,"[%s]\tScheduler thread moved from state running -> sleep : No space to load new links in queue",__func__);
                std::this_thread::sleep_for(chrono::seconds(LNKSHDSLEEPINT));       // sleep for sometime
                log->write(LOG::INFO,"[%s]\tScheduler thread moved from state sleep -> running",__func__);
            }
        }
    }
    catch(__QUEUE__::QueueException &e)
    {
        queLock.unlock();
        throw __QUEUE__::QueueException(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }

}

void DataServer::shutdown()
{
    try
    {   log->write(LOG::INFO,"[%s]\tShutting down DataServer",__func__);
        runScheduler=false;
        log->write(LOG::INFO,"[%s]\tLink Scheduler thread stopped",__func__);

        que->writeInstance();
        if(que->writeInstance()==WRITE_SUCC)
            log->write(LOG::INFO,"[%s]\tQueue current instance saved successfully ",__func__);

        se->shutdown();
        log->write(LOG::INFO,"[%s]\tStorage Engined stopped",__func__);
        indx->shutdown();
        log->write(LOG::INFO,"[%s]\tIndexer stopped",__func__);

        stop=true;
        ClientConnection close(DSIpAddress,DSPort);

        for(auto ittr=masters.begin();ittr!=masters.end();ittr++)
        {
            delete *ittr;
        }
        masters.clear();
        log->write(LOG::INFO,"[%s]\tDataServer stopped",__func__);
    }
    catch(__QUEUE__::QueueException &e)
    {
        log->write(LOG::WARNING,"[%s]\tFailed to save current instance of queue\nException : %s",__func__,e.what());
    }
    catch(std::exception &e)
    {
        log->write(LOG::ERROR,"[%s]\tException : %s",__func__,e.what());
    }

}

string DataServer::statusInfo()
{
    json sData;
    sData["ipaddress"]=DSIpAddress;
    sData["port"]=DSPort;

    json strEng;
    strEng["splitfactor"]=CONFIG::STORAGEENGINE_SPLITFACTOR;
    strEng["cachesize"]=CONFIG::STORAGEENGINE_CACHESIZE;
    strEng["totallinks"]=se->getTotalLinks();

    json ind;
    ind["cachesize"]=CONFIG::INDEXER_CACHESIZE;
    ind["datafile"]=CONFIG::INDEXER_DATAFILE;

    sData["storage_engine"]=strEng;
    sData["indexer"]=ind;

    json masterInfo=NULL;
    lockMasterList.lock();
    int cnt=0;
    for(auto ittr=masters.begin();ittr!=masters.end();ittr++)
    {
        masterInfo["ipaddress"]=(*ittr)->mIpAddress;
        masterInfo["port"]=(*ittr)->mPort;
        masterInfo["httpipaddress"]=(*ittr)->mHttpAddress;
        masterInfo["httpport"]=(*ittr)->mHttpPort;
        masterInfo["registered_time"]=(*ittr)->rTime;
        sData["CRSMaster"][cnt++]=masterInfo;
    }

    lockMasterList.unlock();
    return sData.dump(1);
}


