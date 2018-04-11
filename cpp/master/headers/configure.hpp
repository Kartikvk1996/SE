/* Dependencies:
1.  json library
*/
#define CONFIGFILE "config.ini"

class CONFIG
{
public:

    static unsigned int LBUFFERSIZE;
    static unsigned int MBUFFERSIZE;
    static unsigned int SBUFFERSIZE;

    static string CMS_IPADDRESS;
    static unsigned short int CMS_PORT;
    static string CMS_LOG_NAME;
    static bool CMS_LOGGING;
    static bool CMS_LOGGING_DATEWISE;
    static bool CMS_CONSOLE_LOGGING;
    static bool CMS_CONSOLE_COLOURING;
    static unsigned short MAXCRAWLERS;

    static string QUEUE_LOG_NAME;
    static string QUEUE_DATAFILE;
    static bool QUEUE_LOGGING;
    static bool QUEUE_LOGGING_DATEWISE;
    static bool QUEUE_CONSOLE_LOGGING;
    static bool QUEUE_CONSOLE_COLOURING;
    static unsigned short QUEUE_SIZE;

    static string HTTPSERVER_IPADDRESS;
    static unsigned short int HTTPSERVER_PORT;
    static string HTTPSERVER_LOG_NAME;
    static bool HTTPSERVER_LOGGING;
    static bool HTTPSERVER_LOGGING_DATEWISE;
    static bool HTTPSERVER_CONSOLE_LOGGING;
    static bool HTTPSERVER_CONSOLE_COLOURING;
    static unsigned short HTTPSERVER_THREADPOOLSIZE;

    static string SOCKET_LOGFILE;
    static bool SOCKET_LOGGING;
    static bool SOCKET_LOGDATEWISE;
    static bool SOCKET_CNSLOGGING;
    static bool SOCKET_CNSCOLOURING;

    static unsigned short MAXCONNECTION;


private:
    static int __init;

private:
    static int __initializer()
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
            CMS_IPADDRESS=pData["MASTER"]["IP"];
            CMS_PORT=pData["MASTER"]["PORT"];
            MAXCONNECTION=pData["MASTER"]["MAXCONNECTION"];
            MAXCRAWLERS=pData["MASTER"]["MAXCRAWLERS"];
            CMS_LOGGING=pData["MASTER"]["LOGGING"];
            CMS_LOG_NAME=pData["MASTER"]["LOGFILE"];
            CMS_LOGGING_DATEWISE=pData["MASTER"]["CREATE_DATEWISE"];
            CMS_CONSOLE_LOGGING=pData["MASTER"]["CONSOLE_LOGGING"];
            CMS_CONSOLE_COLOURING=pData["MASTER"]["CONSOLE_COLOURING"];


            QUEUE_SIZE=pData["QUEUE"]["SIZE"];
            QUEUE_DATAFILE=pData["QUEUE"]["DATAFILE"];
            QUEUE_LOGGING=pData["QUEUE"]["LOGGING"];
            QUEUE_LOG_NAME=pData["QUEUE"]["LOGFILE"];
            QUEUE_LOGGING_DATEWISE=pData["QUEUE"]["CREATE_DATEWISE"];
            QUEUE_CONSOLE_LOGGING=pData["QUEUE"]["CONSOLE_LOGGING"];
            QUEUE_CONSOLE_COLOURING=pData["QUEUE"]["CONSOLE_COLOURING"];


            LBUFFERSIZE=pData["LBUFFERSIZE"];
            MBUFFERSIZE=pData["MBUFFERSIZE"];
            SBUFFERSIZE=pData["SBUFFERSIZE"];

            HTTPSERVER_IPADDRESS=pData["HTTPSERVER"]["IP"];
            HTTPSERVER_PORT=pData["HTTPSERVER"]["PORT"];
            HTTPSERVER_THREADPOOLSIZE=pData["HTTPSERVER"]["THREADPOOLSIZE"];
            HTTPSERVER_LOGGING=pData["HTTPSERVER"]["LOGGING"];
            HTTPSERVER_LOG_NAME=pData["HTTPSERVER"]["LOGFILE"];
            HTTPSERVER_LOGGING_DATEWISE=pData["HTTPSERVER"]["CREATE_DATEWISE"];
            HTTPSERVER_CONSOLE_LOGGING=pData["HTTPSERVER"]["CONSOLE_LOGGING"];
            HTTPSERVER_CONSOLE_COLOURING=pData["HTTPSERVER"]["CONSOLE_COLOURING"];

            SOCKET_LOGFILE=pData["SOCKET"]["LOGFILE"];
            SOCKET_LOGGING=pData["SOCKET"]["LOGGING"];
            SOCKET_LOGDATEWISE=pData["SOCKET"]["CREATE_DATEWISE"];
            SOCKET_CNSLOGGING=pData["SOCKET"]["CONSOLE_LOGGING"];
            SOCKET_CNSCOLOURING=pData["SOCKET"]["CONSOLE_COLOURING"];

        }
        catch(std::exception &e)
        {
            cout<<"Error while parsing configuration file :\n"<<inputData<<"\n\n"<<e.what();
            exit(-1);
        }
        return 0;
    }


};

unsigned int CONFIG::LBUFFERSIZE;
unsigned int CONFIG::MBUFFERSIZE;
unsigned int CONFIG::SBUFFERSIZE;

string CONFIG::CMS_IPADDRESS;
unsigned short int CONFIG::CMS_PORT;
string CONFIG::CMS_LOG_NAME;
bool CONFIG::CMS_LOGGING;
bool CONFIG::CMS_LOGGING_DATEWISE;
bool CONFIG::CMS_CONSOLE_LOGGING;
bool CONFIG::CMS_CONSOLE_COLOURING;
unsigned short CONFIG::MAXCRAWLERS;

string CONFIG::QUEUE_LOG_NAME;
string CONFIG::QUEUE_DATAFILE;
bool CONFIG::QUEUE_LOGGING;
bool CONFIG::QUEUE_LOGGING_DATEWISE;
bool CONFIG::QUEUE_CONSOLE_LOGGING;
bool CONFIG::QUEUE_CONSOLE_COLOURING;
unsigned short CONFIG::QUEUE_SIZE;

string CONFIG::HTTPSERVER_IPADDRESS;
unsigned short int CONFIG::HTTPSERVER_PORT;
string CONFIG::HTTPSERVER_LOG_NAME;
bool CONFIG::HTTPSERVER_LOGGING;
bool CONFIG::HTTPSERVER_LOGGING_DATEWISE;
bool CONFIG::HTTPSERVER_CONSOLE_LOGGING;
bool CONFIG::HTTPSERVER_CONSOLE_COLOURING;
unsigned short CONFIG::HTTPSERVER_THREADPOOLSIZE;


string CONFIG::SOCKET_LOGFILE;
bool CONFIG::SOCKET_LOGGING;
bool CONFIG::SOCKET_LOGDATEWISE;
bool CONFIG::SOCKET_CNSLOGGING;
bool CONFIG::SOCKET_CNSCOLOURING;

unsigned short CONFIG::MAXCONNECTION;
int CONFIG::__init=CONFIG::__initializer();
