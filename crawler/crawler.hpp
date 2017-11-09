#include"crawler_init.hpp"
#include<mutex>

class Worker;

class Crawler: public CrawlerInitialize
{

private:
    unsigned int CrawlerId;
    bool isConnected;
    unsigned short int fileId;

    mutex mtx;
    mutex queueLock;

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
    void run();                     /* To spawn worker threads */
    void display();                 /* Displays crawlers status */
    
    /* To Send links to master */
    bool sendLinks();         
    
    /* To request links from master */
    string requestLinks();

    /* To Send updated information to master */
    bool updateLinks();
    
    /* Parse input data from master */
    bool parseRequest(string);
    bool sendAcknowledge();
    void writeCurrentInstance();

public:
    void* listenToMaster();
    void stopCrawler();
    unsigned short int getFileId();
    /* To give links to worker threads */
    string getLocalLinks();

};

void Crawler::run()
{
    thread worker[this->maxWorkerThreads];
    Worker *dw[this->maxWorkerThreads];
    for(int ittr=0;ittr<maxWorkerThreads;ittr++)
    {
        dw[ittr]=new doWork(string("Thread "+ittr),this);
        worker[ittr]=thread(doWork::crawl,dw[ittr]);
        worker[ittr].detach();
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

string Crawler::getLocalLinks()
{
    lockQueue.lock();
    // get link from queue;

    lockQueue.unlock();
    //return data
}
