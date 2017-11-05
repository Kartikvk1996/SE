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
    #pragma comment(std=c++11)
    #pragma comment(-lpthread)

#include<iostream>
#include<bits/stdc++.h>
#include<string>
#include<string.h>
#include<ctime>
#include<stdlib.h>
#include<stdbool.h>
#include<thread>

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


#include"../lib/connection.hpp"
#include"master.hpp"

/*---------------------------------------------------END OF FUNCTION DEFINITIONS--------------------------------------------------------------------------------*/

int main()
{
    Master *m=new Master();

    thread listen (&Master::listenToCrawlers,m);
    thread display (&Master::display,m);
    listen.join();
}
