
/*----------------------------------------------CLASS DECLARATION AND IMPLEMENTATION-----------------------------------------------------------*/

class CrawlerInitialize
{
private:
    unsigned short int port;
    string ipAddress;
    string masterIpAddress;
    unsigned short int masterPort;
    unsigned short int maxWorkerThreads;
    string startTime;
    unsigned int sendQueueSize;
    unsigned int receiveQueueSize;
public:
    ServerConnection *server;
    ClientConnection *client;

    // constructor
    CrawlerInitialize()
    {
        this->port=0;
        this->maxWorkerThreads=0;
        this->sendQueueSize=0;
        this->receiveQueueSize=0;
        this->startTime="";
        this->ipAddress="";
    }

public:
    bool init();
    void display();
    void start();
    void stop();
    bool write_log(string);
    string getMasterIp();
    unsigned short int getMasterPort();
};


/*-----------------------------------------IMPLEMENTATION OF CrawlerInitialize CLASS FUNCTIONS----------------------------------------------*/


/*  This function configures the server port and maximum number of workerThreads
    it should run.

    The init() function return boolean value true if all variables are configured
    else false if failed to initialize the variables
*/
bool CrawlerInitialize::init()
{
    FILE *fp=fopen(CONFIG_FILE_PATH,"r");
    if(fp==NULL)
    {
        #ifdef DEBUG
        {
            cout<<"Failed to open config.txt file\n";
        }
        #endif
        CrawlerInitialize::write_log("Failed to open config.txt file to initialize");
        return false;
    }

    /* allocation memory to store file values   */
    char *buffer=(char *)malloc(sizeof(char)*1024);
    char arr[10][30],ittr=0;
    while(!feof(fp))
    {
        fscanf(fp,"%s",buffer);
        fscanf(fp,"%s",buffer);
        strcpy(arr[ittr++],buffer);   /* Read information to array */
    }

    this->ipAddress=arr[0];
    this->port=(unsigned short int)atoi(arr[1]);
    this->masterIpAddress=arr[2];
    this->masterPort=(unsigned short int)atoi(arr[3]);
    this->maxWorkerThreads=atoi(arr[4]);
    this->sendQueueSize=atoi(arr[5]);
    this->receiveQueueSize=atoi(arr[6]);
    getTime(this->startTime);

    free(buffer);
    fclose(fp);
    CrawlerInitialize::write_log("Crawler variables initialized successfully");
    return true;
}


/*  This function displays all the initialized variables value
*/
void CrawlerInitialize::display()
{
    cout<<"\n\n\n\t\t\t\tCRAWLER INFORMATION\n\n";
    cout<<"\n\t\t\tIPADDRESS    :   "<<this->ipAddress;
    cout<<"\n\t\t\tPORT     :   "<<this->port;
    cout<<"\n\t\t\tMAXIMUM WORKERS SUPPORT    :   "<<this->maxWorkerThreads;
    cout<<"\n\t\t\tSEND QUEUE SIZE   :   "<<this->sendQueueSize;
    cout<<"\n\t\t\tRECEIVE QUEUE SIZE   :   "<<this->receiveQueueSize;
    cout<<"\n\t\t\tSTART TIME   :   "<<this->startTime<<"\n\n";
    return;
}

/*  This function writes the each stage status in log.txt file
*/
bool CrawlerInitialize::write_log(string message)
{
    FILE *fp=fopen(LOG_FILE_PATH,"a+");
    if(fp==NULL)
    {
        #ifdef DEBUG
        {
            cout<<"Failed to open log.txt file \n";
        }
        #endif // DEBUG
        return false;
    }

    /* Attach time stamp */
    string writeMessage;
    getTime(writeMessage);
    writeMessage+="\t"+message+"\n";
    fprintf(fp,"%s",writeMessage.c_str());
    fclose(fp);
    return true;
}

/*  This function establish the TCP socked via particular port
    to accept the incoming requests
*/
void CrawlerInitialize::start()
{
    server=new ServerConnection(this->ipAddress,this->port,10);
    if(server->isAlive())
        CrawlerInitialize::write_log("Crawler server started");
    else
        CrawlerInitialize::write_log("Failed to start server");
    client=new ClientConnection(this->masterIpAddress,this->masterPort);
    if(client->isAlive())
        CrawlerInitialize::write_log("Crawler connected to Master");
    else
        CrawlerInitialize::write_log("Failed to connected to Master");
}

/* This function stops the server
*/
void CrawlerInitialize::stop()
{
    if(server->isAlive())
        server->closeConnection();
    CrawlerInitialize::write_log("Crawler server stopped");
    if(client->isAlive())
        client->closeConnection();
    CrawlerInitialize::write_log("Crawler successfully disconnected from Master");
}


string CrawlerInitialize::getMasterIp()
{
    return this->masterIpAddress;
}

unsigned short int CrawlerInitialize::getMasterPort()
{
    return this->masterPort;
}

/*---------------------------------------------------END OF CLASS FUNCTIONS IMPLEMENTATIONS--------------------------------------------------------------*/
