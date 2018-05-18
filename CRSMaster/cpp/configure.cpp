#include"../headers/configure.hpp"


int CONFIG::__initializer()
{
    std::ifstream in(CONFIGFILE);
    if(in.is_open()==false)
    {
        cout<<"Failed to open : "<<CONFIGFILE<<"\n";
        exit(-1);
    }

    string inputData((std::istreambuf_iterator<char>(in) ),(std::istreambuf_iterator<char>()));
    in.close();
    json pData;
    try
    {
        pData=json::parse(inputData);

        LOGGING=pData["LOGGER"]["LOGGING"];
        LOG_FILENAME=pData["LOGGER"]["LOGFILE"];
        LOG_CREATEDATEWISE=pData["LOGGER"]["CREATE_DATEWISE"];
        LOG_TOCONSOLE=pData["LOGGER"]["CONSOLE_LOGGING"];
        LOG_CNSCOLOR=pData["LOGGER"]["CONSOLE_COLOURING"];


        DATASERVER_IPADDRESS=pData["DATASERVER"]["IP"];
        DATASERVER_PORT=pData["DATASERVER"]["PORT"];

        CMS_IPADDRESS=pData["MASTER"]["IP"];
        CMS_PORT=pData["MASTER"]["PORT"];
        MAXCONNECTION=pData["MASTER"]["MAXCONNECTION"];
        CMS_MAXCRAWLERS=pData["MASTER"]["MAXCRAWLERS"];

        QUEUE_SIZE=pData["QUEUE"]["SIZE"];
        QUEUE_DATAFILE=pData["QUEUE"]["DATAFILE"];

        LBUFFERSIZE=pData["LBUFFERSIZE"];
        MBUFFERSIZE=pData["MBUFFERSIZE"];
        SBUFFERSIZE=pData["SBUFFERSIZE"];

        HTTPSERVER_IPADDRESS=pData["HTTPSERVER"]["IP"];
        HTTPSERVER_PORT=pData["HTTPSERVER"]["PORT"];
        HTTPSERVER_THREADPOOLSIZE=pData["HTTPSERVER"]["THREADPOOLSIZE"];
        HTTPSERVER_HTDOCS=pData["HTTPSERVER"]["HTDOCS_PATH"];

    }
    catch(std::exception &e)
    {
        cout<<"Error while parsing configuration file :\n"<<inputData<<"\n\n"<<e.what();
        exit(-1);
    }
    return 0;
}

unsigned int CONFIG::LBUFFERSIZE;
unsigned int CONFIG::MBUFFERSIZE;
unsigned int CONFIG::SBUFFERSIZE;

bool CONFIG::LOGGING;
string CONFIG::LOG_FILENAME;
bool CONFIG::LOG_CREATEDATEWISE;
bool CONFIG::LOG_TOCONSOLE;
bool CONFIG::LOG_CNSCOLOR;

string CONFIG::DATASERVER_IPADDRESS;
unsigned short int CONFIG::DATASERVER_PORT;

string CONFIG::CMS_IPADDRESS;
unsigned short int CONFIG::CMS_PORT;
unsigned short CONFIG::CMS_MAXCRAWLERS;

unsigned short CONFIG::QUEUE_SIZE;
string CONFIG::QUEUE_DATAFILE;

string CONFIG::HTTPSERVER_IPADDRESS;
unsigned short int CONFIG::HTTPSERVER_PORT;
unsigned short CONFIG::HTTPSERVER_THREADPOOLSIZE;
string CONFIG::HTTPSERVER_HTDOCS;


unsigned short CONFIG::MAXCONNECTION;
int CONFIG::__init=CONFIG::__initializer();

