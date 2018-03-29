#include"../lib/mut_locks.hpp"
#include"master_init.hpp"
#include"../lib/json.hpp"

using json = nlohmann::json;

#define THPOOLSIZE 10
#define POLLINTERVAL 10

class CMS : public CMSInitialize
{

public:
    // to be implemented Queue *que;


public:
    CMS()
    {
        if(!this->init())
        {
            exit(-1);  // Failed to Start CMS server
        }



        // reading previously saved queue status
        /* to be implemented
        que=new Queue(this->queueSize);
        char buf[BUFSIZ];
        sprintf(buf,"Queue instance created with max size %hu",this->queueSize);
        this->write_log(buf);
        if(que->readInstance()==true)
        {
            sprintf(buf,"Queue previous saved instance loaded successfully with %lu links",que->currQueueSize);
            this->write_log(buf);
        }
        else
        {
            this->write_log("Queue Failed to load previous saved instance");
        }
        */

    }

    ~CMS()
    {
        this->stop();
    }

private:
    struct crawler
    {
        unsigned int crawlerQueueSize;
        string crawlerIpAddress;
        unsigned short int crawlerPort;
        string crawlerJoinTime;
        unsigned short int crawlerAlive;
    };

    list<crawler> crawlerInfo;


public:
//    string getQueueLinks();
//    bool addQueueLinks();
    unsigned int getUrlId();
    bool isUrlPresent();
    bool addCrawler(CSOCKET,json);
    bool removeCrawler(string,unsigned short int);
    string requestLinks();          /* Requests links from crawler  */
    void sendLinks(CSOCKET,json);   /* Send links to crawlers       */
    void* listenToCrawlers();
    json parseRequest(string);
    bool sendAcknowledge(int,json);
    void displayCrawlersInfo();
    string statusInfo();             /* Sends information in JSON format via HTTP*/
    void response(int);
    void pollCrawlers(short int,string,unsigned short int);
};



void * CMS::listenToCrawlers()
{
    while(true)
    {
        CSOCKET fd=server->acceptConnection();
        if(fd<0)
        {
            cout<<"ERROR Failed to Accept Connection\t"<<__func__<<"\n";
        }
        else
        {
            cout<<"New Incoming Request\n";
            std::thread worker(&CMS::response,this,fd);
            worker.detach();
        }
    }
}

void CMS::response(int fd)
{

    json pData;
    string inpData;

    inpData=server->readData(fd);
    cout<<"IP Data : "<<inpData<<"\n\n";

    try
    {
        pData=CMS::parseRequest(inpData);
    }
    catch(std::exception &e)
    {
        cout<<"Exception :\n"<<e.what();
        return ;
    }
    int reqType;

    if(inpData!="NULL")
    {
        reqType=pData["type"];
        switch(reqType)
        {
            case CRS_CONNECT:   this->write_log("Adding new CRS system");
                                CMS::addCrawler(fd,pData);
                                shutdown(fd,SHUT_WR);
                                break;

                                        break;
            case CRS_SHUTDOWN:          break;
           // case CMS_POLL:              break;
            case CRS_REQ_NLINKS:		this->sendLinks(fd,pData);
            							break;
            case CMS_SND_NLINKS_SUCC:
										break;

            case CRS_POLL_ALIVE:        {
                                            string ip=pData["ipaddress"];
                                            unsigned int port=pData["port"];
                                            CMS::pollCrawlers(1,ip,port);
                                            break;
                                        }
            case CRS_DISCONNECT:        break;
            case CRS_UPDT_LINKS:        break;
            case CRS_STATE_UPDT:        break;
            default: "Invalid Request to System"; break;
        }
    }

}

bool CMS::addCrawler(int fd,json pData)
{
    // block other threads
    std::lock_guard<std::mutex> lock(mutex_crawlerInfo);

    char buffer[BUFSIZ];
    bool dupCrawler=false;
    // Copy all information about CRS
    crawler newCrawler;
    newCrawler.crawlerIpAddress=pData["ipaddress"];
    newCrawler.crawlerPort=pData["port"];
    getTime(newCrawler.crawlerJoinTime);
    newCrawler.crawlerQueueSize=pData["maxqueuesize"];
    newCrawler.crawlerAlive=0;

    json response;

    if(crawlerInfo.size()>=this->maxCrawlers)  // checks if max CRS limit is exceeded
    {
        response["mode"]=RESPONSE;
        response["type"]=CRS_CONN_LMTEXC;
        snprintf(buffer,sizeof(buffer),"Failed to add new CRS IP %s:%d limit exceed.\t FUNC -> %s",newCrawler.crawlerIpAddress.c_str(),newCrawler.crawlerPort,__func__);
        this->write_log(buffer);
        if(!this->sendAcknowledge(fd,response.dump()))
        {
            snprintf(buffer,sizeof(buffer),"Failed to send Acknowledgement to IP : %s:%d .\t FUNC -> %s",newCrawler.crawlerIpAddress.c_str(),newCrawler.crawlerPort,__func__);
            this->write_log(buffer);
        }
    }
    else
    {
        // following code checks for the duplication of same IP and PORT
        for(auto cr=crawlerInfo.begin();cr!=crawlerInfo.end();cr++)
        {
            if((cr->crawlerIpAddress==newCrawler.crawlerIpAddress) && (cr->crawlerPort==newCrawler.crawlerPort))
            {
                getTime(cr->crawlerJoinTime);
                dupCrawler=true;
            }
        }

        // Send the response to the CRS (crawler)
        response["mode"]=RESPONSE;
        response["type"]=CRS_CONN_SUCC;
        if(!this->sendAcknowledge(fd,response.dump()))
        {
            snprintf(buffer,sizeof(buffer),"Failed to send Acknowledgement to IP : %s:%d .\t FUNC -> %s",newCrawler.crawlerIpAddress.c_str(),newCrawler.crawlerPort,__func__);
            this->write_log(buffer);
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
                    this->write_log("Polling started on CRS (Crawlers)");
                }

                snprintf(buffer, sizeof(buffer), "New Crawler added  IP : %s\tPORT : %hu\tmaxQueueSize : %d", newCrawler.crawlerIpAddress.c_str(),newCrawler.crawlerPort,newCrawler.crawlerQueueSize);
                this->write_log(buffer);
            }
        }

    }

}

json CMS::parseRequest(string data)
{
    return json::parse(data);
}

bool CMS::sendAcknowledge(int fd, json Data)
{
    if(server->writeData(fd,Data.dump()))
        return true;
    else
        return false;
}

bool CMS::removeCrawler(string ip , unsigned short int port)
{
    // block other threads
    std::lock_guard<std::mutex> lock(mutex_crawlerInfo);

    json response;


    char buffer[BUFSIZ];
    // search for the CRS (crawler)
    for(auto cr=crawlerInfo.begin();cr!=crawlerInfo.end();cr++)
    {
        if((cr->crawlerIpAddress==ip) && (cr->crawlerPort==port))
        {
            crawlerInfo.erase(cr);  // remove it
            currCrawlers--;
            snprintf(buffer, sizeof(buffer), "Crawler with IP : %s\tPORT : %hu\tremoved successfully", ip.c_str(),port);
            this->write_log(buffer);
            return true ;
        }
    }
    snprintf(buffer, sizeof(buffer), "Failed to remove Crawler with IP : %s\tPORT : %hu .\t  FUNC -> %s", ip.c_str(),port,__func__);
    this->write_log(buffer);
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

        ClientConnection *pollClient;

        while(true)
        {

            mutex_poll.lock();  // enter critical section

            if(currCrawlers<1)
            {
                mutex_poll.unlock();
                this->write_log("polling stopped , No CRS (crawlers) attached.");
                break;
            }


            for(auto cr=crawlerInfo.begin();cr!=crawlerInfo.end();cr++)
            {
                if(cr->crawlerAlive<3)  // check for 3 attempts
                {
                    pollClient= new ClientConnection(cr->crawlerIpAddress,cr->crawlerPort);
                    //cout<<cr->crawlerIpAddress<<"\n"<<cr->crawlerPort<<"\n";
                    if((pollClient->writeData(pollData.dump()))==false)
                    {
                        CMS::removeCrawler(cr->crawlerIpAddress,cr->crawlerPort);
                        this->write_log("Failed to send polling message to Crawler");
                        break;
                    }
                    else
                    {
                        cr->crawlerAlive=cr->crawlerAlive+1;
                    }
                }
                else
                {
                    CMS::removeCrawler(cr->crawlerIpAddress,cr->crawlerPort);
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
            if((cr->crawlerIpAddress==ip)&& (cr->crawlerPort==port))
            {
                cr->crawlerAlive=0;
                mutex_poll.unlock();
                return;
            }
        }
        mutex_poll.unlock();
        return;
    }

}

void CMS::sendLinks(CSOCKET fd,json inpData)
{
	char buffer[BUFSIZ];
	json response=this->que->getLinks(inpData["nooflinks"]);
	if(!this->sendAcknowledge(fd,response))
    {
        snprintf(buffer,sizeof(buffer),"Failed to send new links to CMS %s",__func__);
        this->write_log(buffer);
    }
    else
    {
    	this->write_log("New links send to CRS successfully");
	}
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
        crData["crawlerIpAddress"]=cr->crawlerIpAddress;
        crData["crawlerPort"]=cr->crawlerPort;
        crData["crawlerJoinTime"]=cr->crawlerJoinTime;
        crData["crawlerAlive"]=cr->crawlerAlive;
        crData["crawlerQueueSize"]=cr->crawlerQueueSize;
        stInfo["crawler"][i++]=crData;
    }
    sendData=stInfo.dump(4);
    return sendData;
}


