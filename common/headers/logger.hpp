/*

@Author : Kartik kalaghatagi


LOG class helps to write log to stdout stream and also to the files specified

*/

#ifndef _STDC_H
#define _STDC_H
#include<bits/stdc++.h>
#endif // _STDC

#ifndef _FORMATTER_H
#include"formatter.hpp"
#endif // _FORMATTER_H

#define _LOGGER_H
#define SEVERITY unsigned short int
#define FMT_BUFFER_SIZE 65535



using namespace std;

namespace __LOGGER__
{
    class LOG
    {
    private:
        enum COLOR
        {
            RED      = 31,
            GREEN    = 32,
            YELLOW     = 33,
            CYAN  = 36
        };

    public:
        static const unsigned short int INFO=LOG::COLOR::GREEN;
        static const unsigned short int WARNING=LOG::COLOR::YELLOW;
        static const unsigned short int ERROR=LOG::COLOR::RED;
        static const unsigned short int CRITICAL=LOG::COLOR::CYAN;

        LOG(string name,bool datewise=false,bool cnsLogging=false,bool consoleColouring=false)
        {
            this->fileName=name;
            this->isDateWise=datewise;
            this->colouring=consoleColouring;
            this->consoleLogging=cnsLogging;

            this->getDate(date);
            __init();
        }

        LOG(string name,bool cnsLogging=false,bool consoleColouring=false)
        {
            this->fileName=name;
            this->colouring=consoleColouring;
            this->consoleLogging=cnsLogging;

            this->getDate(date);
            __init();
        }


    private:
        string fileName;
        bool isDateWise=false;
        bool colouring=false;
        bool consoleLogging =false;
        string date;
        fstream out;

        void getDate(string &);
        void logTime(string &);
        string severityLevel(SEVERITY);
        void __init();
        string formatter(string &,va_list);
        void writeToConsole(SEVERITY ,string );
        void color(SEVERITY,string);

    public:
        void write(SEVERITY ,string ,...);
    };

}

void getTime(string&);
