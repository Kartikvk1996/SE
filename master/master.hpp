#include"master_init.hpp"
#include"../lib/json.hpp"

using json = nlohmann::json;

class Master : public masterInitialize
{

public:
    Master()
    {
        this->init();
        this->start();
    }

    ~Master()
    {
        this->start();
    }

private:
    struct crawler
    {
        unsigned short int crawlerId;
        unsigned int crawlerQueueSize;
        string crawlerIpAddress;
        unsigned short int crawlerPort;
        string crawlerJoinTime;
    };


    vector<crawler> crawlerInfo;


public:
    string getQueueLinks();
    bool addQueueLinks();
    unsigned int getUrlId();
    bool isUrlPresent();
    bool addCrawler();
    bool removeCrawler();
    string requestLinks();          /* Requests links from crawler  */
    bool sendLinks();               /* Send links to crawlers       */
    void* listenToCrawlers();
    bool parseRequest(string);
    void sendAcknowledge();
    void displayCrawlersInfo();

};



string Master::getQueueLinks()
{
    return NULL;
}

void * Master::listenToCrawlers()
{
    while(1)
    {
        cout<<"Listening to client\n";
        string inputData=server->readData();
        write_log("received an incoming request");
        if(!parseRequest(inputData))
        {
            #ifdef DEBUG
                cout<<"Failed to parse request\n"; 
            #endif // DEBUG
	    write_log("Failed to parse request");
        }
        cout<<"Next\n";
    }
}


bool Master::parseRequest(string data)
{
    //parse input data
    // send acknowledge
	json parsedData=json::parse(data);
	if(parsedData["protocol"]=="connect")
    {
        cout<<"Hello bassya";
        return true;
    }
}



