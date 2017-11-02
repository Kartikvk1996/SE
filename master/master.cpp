/*  Author  :   kartik v kalaghatagi
    Date    :   29-10-2017
*/


/*------------------------------------------------------------Libraries----------------------------------------------------------------------*/

#ifdef __unix__
    #include<sys/types.h>
    #include<pthread.h>
    #include<signal.h>
    #include<unistd.h>
    #include<fcntl.h>
#endif // __unix__


#include<iostream>
#include<bits/stdc++.h>
#include<string>
#include<string.h>
#include<ctime>
#include<stdlib.h>
#include<stdbool.h>

#include"connection.hpp"


/*  Enable debug mode
    0   -   off
    1   -   on
*/
#define DEBUG 0


#define CONFIG_FILE_PATH "config.txt"
#define LOG_FILE_PATH "log.txt"



using namespace std;

/*----------------------------------------------------------FUNCTION PROTOTYPE-----------------------------------------------------------------*/

void getTime(string &ctime);
void sighandler(int signum);

/*----------------------------------------------CLASS DECLARATION AND IMPLEMENTATION-----------------------------------------------------------*/

class masterInitialize
{
private:
    unsigned short int port;
    unsigned short int queueSize;
    unsigned short int maxCrawlers;
    string startTime;
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
    return true;
}

/* This function stops the server
*/
bool masterInitialize::stop()
{
    server->closeConnection();
    return true;
}


/*---------------------------------------------------END OF CLASS FUNCTIONS IMPLEMENTATIONS--------------------------------------------------------------*/

masterInitialize *global;

/*---------------------------------------------------FUNCTION DEFINITIONS--------------------------------------------------------------------------------*/

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



void sighandler(int signum)
{
    if(global->stop_server()==true)
    {

        global->write_log("Server stopped successfully");
        exit(EXIT_SUCCESS);
    }
    else
    {
        global->write_log("Failed to stop server properly");
        exit(EXIT_FAILURE);
    }

}


/*---------------------------------------------------END OF FUNCTION DEFINITIONS--------------------------------------------------------------------------------*/


class Master : public masterInitialize
{

private:
    struct crawler
    {
        unsigned short int crawlerId;
        unsigned int crawlerQueueSize;
        string crawlerIpAddress;
        unsigned short int crawlerPost;
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
    void listenToCrawlers();
    bool parseRequest();
    bool sendAcknoledge();
    void displayCrawlersInfo();


};






int main()
{
    /* Signal Handler for interrupt signal*/
    signal(SIGINT, sighandler);

    masterInitialize *m=new masterInitialize();
    masterInitialize *global=m;


    m->write_log("Starting Master");

    /* Initialize Variables */
    if(m->init()==true)
    {
        m->display();   /* Display information */
        m->write_log("Variables Initialized successfully");
    }
    else
    {
        m->write_log("Failed to initialize Variables");
    }

    m->write_log("Starting Server");
    if(m->start_server()==true)
    {
        m->write_log("Server Started successfully");
    }
    else
    {
        m->write_log("Failed to start server");
    }


    while(1)
    {
        // do job
    }

    return 0;
}
