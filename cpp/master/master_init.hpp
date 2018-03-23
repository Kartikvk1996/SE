
/*----------------------------------------------CLASS DECLARATION AND IMPLEMENTATION-----------------------------------------------------------*/

class masterInitialize
{
private:
    unsigned short int port;
    unsigned short int queueSize;
    unsigned short int maxCrawlers;
    string startTime;
public:
    ServerConnection *server;
    ClientConnection *client;


public:
    // constructor
    masterInitialize()
    {
        this->port=0;
        this->maxCrawlers=0;
        this->queueSize=0;
        this->startTime="";
    }

public:
    bool init();
    void display();
    bool start();
    bool stop();
    bool write_log(string);
};

/*-----------------------------------------IMPLEMENTATION OF masterInitialize CLASS FUNCTIONS----------------------------------------------*/


/*  This function configures the server port and maximum number of crawlers
    it can it can handle and also master's URL queue size.

    The init() function return boolean value true if all variables are configured
    else false if failed to initialize the variables
*/
bool masterInitialize::init()
{
    FILE *fp=fopen(CONFIG_FILE_PATH,"r");
    if(fp==NULL)
    {
        #ifdef DEBUG
        {
            cout<<"Failed to open config.txt file\n";
            masterInitialize::write_log("Failed to open config.txt file to initialize");
        }
        #endif
        return false;
    }

    /* allocation memory to store file values   */
    char *buffer=(char *)malloc(sizeof(char)*1024);
    unsigned short int arr[10],ittr=0;
    while(!feof(fp))
    {
        fscanf(fp,"%s",buffer);
        fscanf(fp,"%s",buffer);
        arr[ittr++]=atoi(buffer);   /* Read information to array */
    }

    this->port=arr[0];
    this->maxCrawlers=arr[1];
    this->queueSize=arr[2];
    getTime(this->startTime);

    free(buffer);
    fclose(fp);
    masterInitialize::write_log("master variables initialized successfully");
    return true;
}


/*  This function displays all the initialized variables value
*/
void masterInitialize::display()
{
    cout<<"\n\n\n\t\t\t\tMASTER INFORMATION\n\n";
    cout<<"\n\t\t\tPORT     :   "<<this->port;
    cout<<"\n\t\t\tMAXIMUM CRAWLERS SUPPORT    :   "<<this->maxCrawlers;
    cout<<"\n\t\t\tQUEUE SIZE   :   "<<this->queueSize;
    cout<<"\n\t\t\tSTART TIME   :   "<<this->startTime<<"\n\n";
    return;
}

/*  This function writes the each stage status in log.txt file
*/
bool masterInitialize::write_log(string message)
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

    It return true on successful creation of socket or false
    in case of failure
*/
bool masterInitialize::start()
{
    server=new ServerConnection("127.0.0.1",this->port,this->maxCrawlers);
    masterInitialize::write_log("Master server started");
    return true;
}

/* This function stops the server
*/
bool masterInitialize::stop()
{
    server->closeConnection();
    masterInitialize::write_log("Master server stopped");
    return true;
}


/*---------------------------------------------------END OF CLASS FUNCTIONS IMPLEMENTATIONS--------------------------------------------------------------*/
