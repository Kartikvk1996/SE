#include"crawler_init.hpp"
#include<mutex>


class Crawler: public CrawlerInitialize
{

private:
    unsigned int CrawlerId;
    bool isConnected;
    unsigned short int fileId;

    mutex mtx;


    struct urlInfo
    {
        unsigned int urlId;
        string urlLink;
        unsigned short int depth;
    };

    // Instance for sendQueue<urlInfo>
    // Instance for ReceiveQueue<urlInfo>

public:
    Crawler()
    {
        this->isConnected=false;
        this->CrawlerId=0;
        this->init();
        this->start();
    }

    ~Crawler()
    {
        this->writeCurrentInstance();
        this->stop();
    }


private:
    void display();
    bool sendLinks();
    string requestLinks();
    bool updateLinks();
    bool parseRequest(string);
    bool sendAcknowledge();
    void writeCurrentInstance();

public:
    void* listenToMaster();
    void stopCrawler();
    unsigned short int getFileId();
};


void * Crawler::listenToMaster()
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


unsigned short int Crawler::getFileId()
{
    mtx.lock();     // Block the other threads
    fileId++;       // increment the fileId counter
    mtx.unlock();   // Allow other threads
    return fileId;
}

bool Crawler::parseRequest(string data)
{

}
