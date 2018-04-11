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

/*----------------------------------------------CLASS DECLARATION AND IMPLEMENTATION-----------------------------------------------------------*/

class CMSInitialize
{

public:
    ServerConnection *server;
    ClientConnection *client;
    string ipAddress;
    unsigned short int port;
    unsigned short int queueSize;
    unsigned short int maxCrawlers;
    unsigned short int maxConnections;
    unsigned short int currCrawlers;
    string startTime;


public:
    // constructor
    CMSInitialize()
    {
        this->currCrawlers=0;
        this->ipAddress=CONFIG::CMS_IPADDRESS;
        this->port=CONFIG::CMS_PORT;
        this->maxCrawlers=CONFIG::MAXCRAWLERS;
        this->maxConnections=CONFIG::MAXCONNECTION;
        this->queueSize=CONFIG::QUEUE_SIZE;
        getTime(this->startTime);
        start();
    }

public:
    void start();
    void stop();
    bool write_log(string);
    string systemInfo();
};

/*-----------------------------------------IMPLEMENTATION OF CMSInitialize CLASS FUNCTIONS----------------------------------------------*/

/*  This function establish the TCP socked via particular port
    to accept the incoming requests

    It return true on successful creation of socket or false
    in case of failure
*/
void CMSInitialize::start()
{
    server=new ServerConnection(this->ipAddress,this->port,this->maxConnections);

}

/* This function stops the server
*/
void CMSInitialize::stop()
{
    server->closeConnection();
}


string CMSInitialize::systemInfo()
{
    json sendData,info;
    info["physical used"]=_physical();
    info["Virtual used"]=_virtual();
    sendData["memory"]=info;
    return sendData.dump(5);
}


/*---------------------------------------------------END OF CLASS FUNCTIONS IMPLEMENTATIONS--------------------------------------------------------------*/
