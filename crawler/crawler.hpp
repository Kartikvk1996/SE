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
        this->run();
    }

    ~Crawler()
    {
        this->writeCurrentInstance();
        this->stop();
    }


private:
    void run();
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

void Crawler::run()
{
    thread worker[this->maxWorkerThreads];
    doWork *dw[this->maxWorkerThreads];
    for(int ittr=0;ittr<maxWorkerThreads;ittr++)
    {
        dw[ittr]=new doWork(string("Thread "+ittr),this);
        worker[ittr]=thread(doWork::crawl,dw[ittr]);
        worker[ittr].join();
    }
}


void * Crawler::listenToMaster()
{
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
