class Master
{
public:
    string mIpAddress;
    string mHttpAddress;
    unsigned short mPort;
    unsigned short mHttpPort;
    unsigned long int rTime;
    unsigned int crawlers;
    short poll;
    unsigned int MaxQueueSize;
    Master()
    {
        mPort=crawlers=MaxQueueSize=poll=0;
    }
};
