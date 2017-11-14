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
    mutex writeFileLock;
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
    void parseRequest(string);
    bool sendAcknowledge();

    /* to store current instance to file */
    void writeCurrentInstance();

public:
    void* listenToMaster();
    void stopCrawler();
    unsigned short int getFileId();
    /* To give links to worker threads */
    string getLocalLinks();
    void writeToFile();
};

void Crawler::run()
{
    thread workers[this->maxWorkerThreads];
    Worker *dw[this->maxWorkerThreads];
    for(int ittr=0;ittr<maxWorkerThreads;ittr++)
    {
        dw[ittr]=new Worker(string("Thread "+ittr),this);
        worker[ittr]=thread(Worker::crawl,dw[ittr]);
        worker[ittr].detach();
    }
}


void * Crawler::listenToMaster()
{
    while(1)
    {
        CSOCKET fd=server->acceptConnection();
        thread w (Crawler::parseRequest,this,server->readData(fd));
        w.detach();
    }    
}


unsigned short int Crawler::getFileId()
{
    mtx.lock();     // Block the other threads
    fileId++;       // increment the fileId counter
    mtx.unlock();   // Allow other threads
    return fileId;
}

void Crawler::parseRequest(string data)
{

}

string Crawler::getLocalLinks()
{
    lockQueue.lock();
    // get link from queue;

    lockQueue.unlock();
    //return data
}


void Crawler::writeToFile(unsigned long int urlId,string url,unsigned long int hash,unsigned int depth,string fileName)
{
    writeFileLock.lock();
    FILE *fp=fopen("urlInfo.txt","a+");
    if(fp==NULL)
        throw "File 'urlInfo.txt' Not Found";
    if(fprintf(fp,"%ld %s %ld %d %s",urlId,url,hash,depth,fileName)==-1)
        throw "Failed to write to urlInfo.txt file\n";

    catch(const char * e)
    {
        cout<<e<<"\n";
    }
    fclose(fp);
    writeFileLock.unlock();
    return;
}



