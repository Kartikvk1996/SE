#define _CONFIGURE_H
#define CONFIGFILE "config.ini"

#ifndef _STDC_H
#define _STDC_H
#include<bits/stdc++.h>
#endif // _STDC

#ifndef _JSON_H
#define _JSON_H
#include"../../common/headers/json.hpp"
#endif // _JSON_H



using namespace std;
using json = nlohmann::json;


class CONFIG
{
public:

    static unsigned int LBUFFERSIZE;
    static unsigned int MBUFFERSIZE;
    static unsigned int SBUFFERSIZE;

    static bool LOGGING;
    static string LOG_FILENAME;
    static bool LOG_CREATEDATEWISE;
    static bool LOG_TOCONSOLE;
    static bool LOG_CNSCOLOR;

    static string CMS_IPADDRESS;
    static unsigned short int CMS_PORT;
    static unsigned short CMS_MAXCRAWLERS;

    static string DATASERVER_IPADDRESS;
    static unsigned short int DATASERVER_PORT;

    static string QUEUE_DATAFILE;
    static unsigned short QUEUE_SIZE;

    static string HTTPSERVER_IPADDRESS;
    static unsigned short int HTTPSERVER_PORT;
    static unsigned short HTTPSERVER_THREADPOOLSIZE;
    static string HTTPSERVER_HTDOCS;

    static unsigned short MAXCONNECTION;

private:
    static int __init;

private:
    static int __initializer();

};
