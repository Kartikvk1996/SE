/* Header dependency
    mut_locks.hpp
    master_init.hpp
    json.hpp
*/


#define POLLINTERVAL 10

class CMS : public CMSInitialize
{

public:
    Queue *que;

private:
    LOG *log;
    bool stop=false;

public:
    CMS()
    {

        if(CONFIG::CMS_LOGGING==true)
            #define CMS_LOGGING_ENABLED

        log=new LOG(CONFIG::CMS_LOG_NAME,CONFIG::CMS_LOGGING_DATEWISE,CONFIG::CMS_CONSOLE_LOGGING,CONFIG::CMS_CONSOLE_COLOURING);


        // reading previously saved queue status
        que=new Queue(this->queueSize);
        log->write(LOG::INFO,"[%s]\tQueue instance created",__func__);
        if(que->readInstance()==true)
        {
            log->write(LOG::INFO,"[%s]\tQueue previous saved instance loaded successfully ",__func__);
        }
        else
        {
            log->write(LOG::ERROR,"[%s]\tQueue Failed to load previous saved instance",__func__);
        }
    }

    ~CMS()
    {
        delete que;
        delete server;
        delete log;
    }

private:
    list<Crawler *> crawlerInfo;


public:
//    string getQueueLinks();
//    bool addQueueLinks();
    unsigned int getUrlId();
    bool isUrlPresent();
    void addCrawler(CSOCKET,json);
    bool removeCrawler(string,unsigned short int);
    string requestLinks();          /* Requests links from crawler  */
    void sendLinks(CSOCKET,json);   /* Send links to crawlers       */
    void* listenToCrawlers();
    json parseRequest(string &);
    bool sendResponse(int,json&);
    bool sendResponse(string&,unsigned short int&, json&);
    string statusInfo();             /* Sends information in JSON format via HTTP*/
    void serveRequest(int);
    void pollCrawlers(short int,string,unsigned short int);
    void shutdown();
};

void * CMS::listenToCrawlers()
{
    while(!stop)
    {
        CSOCKET fd=server->acceptConnection();
        if(fd<0)
        {
            log->write(LOG::ERROR,"[%s]\tFailed to accept new incoming request %d",__func__,fd);
        }
        else
        {
            std::thread worker(&CMS::serveRequest,this,fd);
            worker.join();
        }
    }
    return NULL;
}

void CMS::serveRequest(int fd)
{
    json pData;
    string inpData;

    inpData=server->readData(fd);
    std::this_thread::sleep_for(chrono::seconds(5));

    try
    {
        pData=CMS::parseRequest(inpData);
    }
    catch(std::exception &e)
    {
        log->write(LOG::WARNING,"[%s]\tException : %s\n%s",__func__,e.what(),inpData.c_str());
        return;
    }
    int reqType;

    if(inpData!="NULL")
    {
        reqType=pData["type"];
        switch(reqType)
        {
            case CRS_CONNECT:           CMS::addCrawler(fd,pData);
                                        break;

            case CRS_SHUTDOWN:          break;
            case CRS_REQ_NLINKS:		this->sendLinks(fd,pData);

            							break;

            case CRS_POLL_ALIVE:        {
                                            string ip=pData["ipaddress"];
                                            unsigned int port=pData["port"];
                                            CMS::pollCrawlers(1,ip,port);
                                            break;
                                        }
            case CRS_DISCONNECT:        {
                                            string ip=pData["ipaddress"];
                                            unsigned int port=pData["port"];
                                            CMS::removeCrawler(ip,port);
                                        }
                                        break;
            case CRS_UPDT_LINKS:        break;
            case CRS_STATE_UPDT:        break;
            default: cout<<"Invalid Request to System"; break;
        }
    }
    close(fd);
    return;

}

void CMS::addCrawler(int fd,json pData)
{
    // block other threads
    std::lock_guard<std::mutex> lock(mutex_crawlerInfo);

    bool dupCrawler=false;
    // Copy all information about CRS
    Crawler *newCrawler = new Crawler();
    newCrawler->crawlerIpAddress=pData["ipaddress"];
    newCrawler->crawlerPort=pData["port"];
    getTime(newCrawler->crawlerJoinTime);
    newCrawler->crawlerQueueSize=pData["maxqueuesize"];
    newCrawler->crawlerAlive=0;

    json response;
    log->write(LOG::INFO,"[%s]\tAdding new crawler",__func__);
    if(crawlerInfo.size()>=this->maxCrawlers)  // checks if max CRS limit is exceeded
    {
        delete newCrawler; // No longer required
        response["mode"]=RESPONSE;
        response["type"]=CRS_CONN_LMTEXC;
        log->write(LOG::CRITICAL,"[%s]\tFailed to add new CRS IP %s:%d , limit exceed",__func__,newCrawler->crawlerIpAddress.c_str(),newCrawler->crawlerPort);

        if(!this->sendResponse(fd,response))
        {
            log->write(LOG::CRITICAL,"[%s]\tFailed to send LIMIT_EXCEED ack to IP : %s:%d",__func__,newCrawler->crawlerIpAddress.c_str(),newCrawler->crawlerPort);
        }
    }
    else
    {
        // following code checks for the duplication of same IP and PORT
        for(auto cr=crawlerInfo.begin();cr!=crawlerInfo.end();cr++)
        {
            if(((*cr)->crawlerIpAddress==newCrawler->crawlerIpAddress) && ((*cr)->crawlerPort==newCrawler->crawlerPort))
            {
                getTime((*cr)->crawlerJoinTime);
                dupCrawler=true;
                delete newCrawler;
            }
        }

        // Send the response to the CRS (crawler)
        response["mode"]=RESPONSE;
        response["type"]=CRS_CONN_SUCC;
        if(!this->sendResponse(fd,response))
        {
            log->write(LOG::CRITICAL,"[%s]\tFailed to send CONNECT ack to IP : %s:%d",__func__,newCrawler->crawlerIpAddress.c_str(),newCrawler->crawlerPort);
            delete newCrawler;
        }
        else
        {
            if(!dupCrawler)
            {
                crawlerInfo.push_back(newCrawler);
                currCrawlers++;
                // start polling the crawlers
                if(currCrawlers==1)
                {
                    std::thread pollThread(&CMS::pollCrawlers,this,0,"",0);
                    pollThread.detach();
                    log->write(LOG::INFO,"[%s]\tpolling stated on crawlers (CRS)",__func__);
                }
                log->write(LOG::INFO,"[%s]\tNew Crawler added IP : %s\tPORT : %hu\tQueueSize : %d",__func__,newCrawler->crawlerIpAddress.c_str(),newCrawler->crawlerPort,newCrawler->crawlerQueueSize);
            }
        }

    }
    return;
}

json CMS::parseRequest(string& data)
{
    return json::parse(data);
}

bool CMS::sendResponse(int fd, json& Data)
{
    if(server->writeData(fd,Data.dump()))
        return true;
    else
        return false;
}

bool CMS::sendResponse(string& ip,unsigned short int &port, json& Data)
{
    ClientConnection c(ip,port);
    if(c.writeData(Data.dump()))
        return true;
    else
        return false;
}

bool CMS::removeCrawler(string ip , unsigned short int port)
{
    // block other threads
    std::lock_guard<std::mutex> lock(mutex_crawlerInfo);

    json response;
    // search for the CRS (crawler)
    for(auto cr=crawlerInfo.begin();cr!=crawlerInfo.end();cr++)
    {
        if(((*cr)->crawlerIpAddress==ip) && ((*cr)->crawlerPort==port))
        {
            delete *cr;  // delete the crawler
            crawlerInfo.erase(cr);  // remove it from the list
            currCrawlers--;
            log->write(LOG::INFO,"[%s]\tCrawler with IP : %s\tPORT : %hu\tremoved successfully",__func__,ip.c_str(),port);
            return true ;
        }
    }
    log->write(LOG::WARNING,"[%s]\tFailed to remove Crawler with IP : %s\tPORT : %hu , No information found.",__func__,ip.c_str(),port);
    return false;
}

void CMS::pollCrawlers(short int result=0,string ip="",unsigned short int port=0)
{
    // if result is 1 then there is a response from CRS (Crawler)
    if(result==0)
    {
        json pollData;
        pollData["mode"]=REQUEST;
        pollData["type"]=CMS_POLL;

        while(true)
        {

            mutex_poll.lock();  // enter critical section

            if(currCrawlers<1)
            {
                mutex_poll.unlock();
                log->write(LOG::INFO,"[%s]\tpolling stopped , No CRS (crawlers) attached",__func__);
                break;
            }


            for(auto cr=crawlerInfo.begin();cr!=crawlerInfo.end();cr++)
            {
                if((*cr)->crawlerAlive<3)  // check for 3 attempts
                {
                    if((sendResponse((*cr)->crawlerIpAddress,(*cr)->crawlerPort,pollData))==false)
                    {
                        log->write(LOG::WARNING,"[%s]\tFailed to send poll message to crawler %s:%p",__func__,(*cr)->crawlerIpAddress,(*cr)->crawlerPort);
                        CMS::removeCrawler((*cr)->crawlerIpAddress,(*cr)->crawlerPort);
                        break;
                    }
                    else
                    {
                        (*cr)->crawlerAlive=(*cr)->crawlerAlive+1;
                    }
                }
                else
                {
                    CMS::removeCrawler((*cr)->crawlerIpAddress,(*cr)->crawlerPort);
                    break;
                }

            }
            mutex_poll.unlock();
            std::this_thread::sleep_for(chrono::seconds(POLLINTERVAL));

        }
    }
    else
    {
        mutex_poll.lock();
        for(auto cr=crawlerInfo.begin();cr!=crawlerInfo.end();cr++)
        {
            if(((*cr)->crawlerIpAddress==ip)&& ((*cr)->crawlerPort==port))
            {
                (*cr)->crawlerAlive=0;
                break;
            }
        }
        mutex_poll.unlock();
        return;
    }

}

void CMS::sendLinks(CSOCKET fd,json inpData)
{
	string ip=inpData["ipaddress"];
	unsigned short int port=inpData["port"];
	json response=this->que->getLinks(inpData["nooflinks"]);
	if((sendResponse(ip,port,response))==false)
    {
        log->write(LOG::WARNING,"[%s]\tFailed to send new links to crawler",__func__);
    }
    else
    {
    	log->write(LOG::INFO,"[%s]\tNew links sent to crawler successfully",__func__);
	}
	return;
}

string CMS::statusInfo()
{
    unsigned short int i=0;

    json stInfo,crData;
    string sendData;
    stInfo["CMS Server IP"]=this->ipAddress;
    stInfo["CMS Server Port"]=this->port;
    stInfo["CMS queue size"]=this->queueSize;
    stInfo["CMS max Crawlers"]=this->maxCrawlers;
    stInfo["CMS Start time"]=this->startTime;
    stInfo["Maximum CRS"]=this->maxCrawlers;

    i=0;
    stInfo["current CRS"]=this->currCrawlers;
    stInfo["crawler"][i]=NULL;
    for(auto cr=crawlerInfo.begin();cr!=crawlerInfo.end();cr++)
    {
        crData["crawlerIpAddress"]=(*cr)->crawlerIpAddress;
        crData["crawlerPort"]=(*cr)->crawlerPort;
        crData["crawlerJoinTime"]=(*cr)->crawlerJoinTime;
        crData["crawlerAlive"]=(*cr)->crawlerAlive;
        crData["crawlerQueueSize"]=(*cr)->crawlerQueueSize;
        stInfo["crawler"][i++]=crData;
    }
    sendData=stInfo.dump(4);
    return sendData;
}

void CMS::shutdown()
{
    log->write(LOG::INFO,"[%s]\tShutting down CMS",__func__);
    json sData;
    sData["mode"]=REQUEST;
    sData["type"]=CMS_SHUTDOWN;

    // sending shutdown message to all crawlers;
    for(auto cr=crawlerInfo.begin();cr!=crawlerInfo.end();cr++)
    {
        sendResponse((*cr)->crawlerIpAddress,(*cr)->crawlerPort,sData);
        this->removeCrawler((*cr)->crawlerIpAddress,(*cr)->crawlerPort);
    }
    log->write(LOG::INFO,"[%s]\tShutdown message sent to all crawlers",__func__);
    log->write(LOG::INFO,"[%s]\tWaiting 10 seconds to get response from all crawlers",__func__);
    std::this_thread::sleep_for(chrono::seconds(10));   // wait for 30 seconds till all crawlers respond

    log->write(LOG::INFO,"[%s]\tSaving queue current instance",__func__);
    if(que->writeInstance()==true)
    {
        log->write(LOG::INFO,"[%s]\tQueue current instance saved successfully ",__func__);
    }
    else
    {
        log->write(LOG::ERROR,"[%s]\tFailed to save current instance of queue",__func__);
    }

    log->write(LOG::INFO,"[%s]\tHTTP Server stopped successfully",__func__);
    log->write(LOG::INFO,"[%s]\tCMS stopped",__func__);
    stop=true;
    ClientConnection close(this->ipAddress,this->port);
}
