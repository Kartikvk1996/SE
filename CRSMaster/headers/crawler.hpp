class Crawler
{
public:
    unsigned int crawlerQueueSize;
    string crawlerIpAddress;
    unsigned short int crawlerPort;
    unsigned short int crawlerHttpPort;
    string crawlerJoinTime;
    unsigned short int crawlerAlive;

    Crawler()
    {
        crawlerAlive=0;
        crawlerPort=0;
        crawlerQueueSize=0;
    }

};
