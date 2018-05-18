#include"../headers/master.hpp"

void getTime(string &ctime)
{
    char buffer[100];
    time_t currentTime;
    struct tm *time_info;
    time(&currentTime);
    time_info=localtime(&currentTime);
    strftime(buffer,100,"%d-%m-%Y %I:%M:%S",time_info);
    ctime=buffer;
    return ;
}

void CMS::__init()
{
    log=new LOG(CONFIG::LOG_FILENAME,CONFIG::LOG_CREATEDATEWISE,CONFIG::LOG_TOCONSOLE,CONFIG::LOG_CNSCOLOR);
    log->write(LOG::INFO,"[%s]\tStarting the Master",__func__);
    try{
        log->write(LOG::INFO,"[%s]\tStarting internal server on IP %s:%d\t",__func__,this->ipAddress.c_str(),this->port);

        server=new ServerConnection(this->ipAddress,this->port,this->maxConnections);
        log->write(LOG::INFO,"[%s]\tInternal server on IP %s:%d started\t",__func__,this->ipAddress.c_str(),this->port);
        log->write(LOG::INFO,"[%s]\tHTTP server on IP %s:%d started\t",__func__,CONFIG::HTTPSERVER_IPADDRESS.c_str(),CONFIG::HTTPSERVER_PORT);

        que=new Queue(this->queueSize);
        log->write(LOG::INFO,"[%s]\tQueue instance created",__func__);

        log->write(LOG::INFO,"[%s]\tQueue loading previous saved instance ",__func__);
        unsigned long int nlinks=que->readInstance();
        log->write(LOG::INFO,"[%s]\tQueue previous saved instance loaded with %u links ",__func__,nlinks);
    }
    catch(__CONNECTION__::SocketException &e)
    {
        log->write(LOG::ERROR,"[%s]\tException : %s",__func__,e.what());
        raise(SIGABRT);
    }
    catch(__QUEUE__::QueueException &e)
    {
        log->write(LOG::WARNING,"[%s]\tException : %s",__func__,e.what());
    }
    catch(std::exception &e)
    {
        log->write(LOG::ERROR,"[%s]\tException : %s",__func__,e.what());
        raise(SIGABRT);
    }
    log->write(LOG::INFO,"[%s]\tMaster started",__func__);
    return;
}

void CMS::connectDataServer()
{
    json j;
    j["mode"]=REQUEST;
    j["type"]=CRSMASTER_CONNECT;
    j["ipaddress"]=this->ipAddress;
    j["port"]=this->port;
    j["httpipaddress"]=CONFIG::HTTPSERVER_IPADDRESS;
    j["httpport"]=CONFIG::HTTPSERVER_PORT;
    j["crawlers"]=0;

    while(true)
    {
        log->write(LOG::INFO,"[%s]\tRegistering with DataServer",__func__);
        try
        {
            ClientConnection c(CONFIG::DATASERVER_IPADDRESS,CONFIG::DATASERVER_PORT);
            c.writeData(j.dump());
            string response=c.readData();
            json res=json::parse(response);
            if(res["type"]==DS_CONNECT_SUCC)
            {
                log->write(LOG::INFO,"[%s]\tSuccessfully Registered with DataServer",__func__);
                break;
            }
        }
        catch(std::exception &e)
        {
            log->write(LOG::CRITICAL,"[%s]\tException caught: %s",__func__,e.what());
        }

        std::this_thread::sleep_for(chrono::seconds(5));
    }
}


void * CMS::listenToCrawlers()
{
    connectDataServer();
    while(!stop)
    {

        try{
            CSOCKET fd=server->acceptConnection();
            std::thread worker(&CMS::serveRequest,this,fd);
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
    return NULL;
}

void CMS::serveRequest(int fd)
{
    json pData;
    string inpData;

    try
    {

        inpData=server->readData(fd);
        pData=CMS::parseRequest(inpData);

        cout<<pData.dump(1)<<"\n";

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

                case SND_NLINKS_SUCC:       {
                                                CMS::addLinks(pData);
                                                break;
                                            }
                case SND_NLINKS_ZERO:       {
                                                    log->write(LOG::INFO,"[%s]\tNo new links are from Data server",__func__);
                                                    break;
                                            }
                                            break;
                case CRS_UPDT_LINKS:        break;
                case CRS_STATE_UPDT:        break;
                case DS_POLL:               sendPollResponse();break;
                default: log->write(LOG::WARNING,"[%s]\tUndefined request\n data : %s",__func__,inpData); break;
            }
        }
        close(fd);
    }
    catch(std::exception &e)
    {
        log->write(LOG::CRITICAL,"[%s]\tException : %s",__func__,e.what());
    }
    return;

}

void CMS::addCrawler(int& fd,json& pData)
{
    // block other threads
    std::lock_guard<std::mutex> lock(mutex_crawlerInfo);

    bool dupCrawler=false;
    // Copy all information about CRS
    Crawler *newCrawler = new Crawler();
    newCrawler->crawlerIpAddress=pData["ipaddress"];
    newCrawler->crawlerPort=pData["port"];
    newCrawler->crawlerHttpPort=pData["httpport"];
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

        try
        {
            this->sendData(fd,response);
        }
        catch(std::exception &e)
        {
            log->write(LOG::CRITICAL,"[%s]\tFailed to send LIMIT_EXCEED ack to IP : %s:%d\nException : %s",__func__,newCrawler->crawlerIpAddress.c_str(),newCrawler->crawlerPort,e.what());
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
        response["dsipaddress"]=CONFIG::DATASERVER_IPADDRESS;
        response["dsport"]=CONFIG::DATASERVER_PORT;
        try
        {
            this->sendData(fd,response);
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
        catch(std::exception &e)
        {
            log->write(LOG::CRITICAL,"[%s]\tFailed to send CONNECT ack to IP : %s:%d\nException : %s",__func__,newCrawler->crawlerIpAddress.c_str(),newCrawler->crawlerPort,e.what());
            delete newCrawler;
        }

    }
    return;
}

json CMS::parseRequest(string& data)
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

void CMS::sendData(int fd, json& Data)
{
    try
    {
        server->writeData(fd,Data.dump());
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

void CMS::sendData(string& ip,unsigned short int &port, json& Data)
{
    try
    {
        cout<<Data.dump(1)<<"\n";
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
                    (*cr)->crawlerAlive=(*cr)->crawlerAlive+1;
                    try
                    {
                        sendData((*cr)->crawlerIpAddress,(*cr)->crawlerPort,pollData);
                        //CMS::removeCrawler((*cr)->crawlerIpAddress,(*cr)->crawlerPort);
                    }
                    catch(std::exception &e)
                    {
                        log->write(LOG::WARNING,"[%s]\tFailed to send poll message to crawler %s:%p\nException : %s",__func__,(*cr)->crawlerIpAddress.c_str(),(*cr)->crawlerPort,e.what());
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
	try
    {
        json response=this->que->getLinks(inpData["nooflinks"]);
        sendData(ip,port,response);
        log->write(LOG::INFO,"[%s]\tNew links sent to crawler successfully",__func__);
    }
    catch(std::exception &e)
    {
    	log->write(LOG::WARNING,"[%s]\tFailed to send new links to crawler\nException : %s",__func__,e.what());
	}
	return;
}

void CMS::requestLinks()
{
    if(this->currCrawlers<=0)
        return;
    log->write(LOG::INFO,"[%s]\tNew link request sent to Data Server",__func__);
    json j;
    j["mode"]=REQUEST;
    j["type"]=CMS_REQ_NLINKS;
    j["nooflinks"]=(que->currQueueSize/this->currCrawlers);
    j["ipaddress"]=this->ipAddress;
    j["port"]=this->port;

        sendData(dsIpAddress,dsPort,j);

    return;
}

string CMS::statusInfo()
{
    unsigned short int i=0;

    json stInfo,crData;
    string outputBuff;
    stInfo["CMS_Server_IP"]=this->ipAddress;
    stInfo["CMS_Server_Port"]=this->port;
    stInfo["CMS_queue_size"]=this->queueSize;
    stInfo["CMS_max_Crawlers"]=this->maxCrawlers;
    stInfo["CMS_Start_time"]=this->startTime;
    stInfo["Maximum_CRS"]=this->maxCrawlers;

    i=0;
    stInfo["current_CRS"]=this->currCrawlers;
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
    outputBuff=stInfo.dump(4);
    return outputBuff;
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
        try
        {
            sendData((*cr)->crawlerIpAddress,(*cr)->crawlerPort,sData);
            this->removeCrawler((*cr)->crawlerIpAddress,(*cr)->crawlerPort);
        }
        catch(std::exception &e)
        {
            log->write(LOG::WARNING,"[%s]\tFailed to send CMS_SHUTDOWN message to crawler %s:%p\nException : %s",__func__,(*cr)->crawlerIpAddress,(*cr)->crawlerPort,e.what());
        }
    }
    log->write(LOG::INFO,"[%s]\tShutdown message sent to all crawlers",__func__);
    log->write(LOG::INFO,"[%s]\tWaiting 10 seconds to get response from all crawlers",__func__);
    std::this_thread::sleep_for(chrono::seconds(10));   // wait for 30 seconds till all crawlers respond

    log->write(LOG::INFO,"[%s]\tSaving queue current instance",__func__);
    try
    {
        if(que->writeInstance()==WRITE_SUCC)
            log->write(LOG::INFO,"[%s]\tQueue current instance saved successfully ",__func__);
    }
    catch(__QUEUE__::QueueException &e)
    {
        log->write(LOG::WARNING,"[%s]\tFailed to save current instance of queue\nException : %s",__func__,e.what());
    }
    catch(std::exception &e)
    {
        log->write(LOG::ERROR,"[%s]\tException : %s",__func__,e.what());
    }
    log->write(LOG::INFO,"[%s]\tMaster stopped",__func__);
    stop=true;
    ClientConnection close(this->ipAddress,this->port);
}

void CMS::addLinks(json pData)
{
    log->write(LOG::INFO,"[%s]\tNew links received from Data server",__func__);
    int nooflinks=que->addLinks(pData);
    log->write(LOG::INFO,"[%s]\t%d new links are added successfully",__func__,nooflinks);
    return;
}

string CMS::systemInfo()
{
    json sendData,info;
    info["physical_used"]=_physical();
    info["virtual_used"]=_virtual();
    sendData["memory"]=info;
    return sendData.dump(5);
}

void CMS::sendPollResponse()
{
    json sData;
    sData["mode"]=RESPONSE;
    sData["type"]=CRSMASTER_POLL_ALIVE;
    sData["ipaddress"]=ipAddress;
    sData["port"]=port;
    ClientConnection c(CONFIG::DATASERVER_IPADDRESS,CONFIG::DATASERVER_PORT);
    c.writeData(sData.dump());
    return;
}
