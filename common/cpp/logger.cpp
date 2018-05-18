#include"../headers/logger.hpp"


using LOG=__LOGGER__::LOG;

const unsigned short int LOG::INFO;
const unsigned short int LOG::WARNING;
const unsigned short int LOG::ERROR;
const unsigned short int LOG::CRITICAL;


void LOG::getDate(string &ctime)
{
    char buffer[20];
    time_t currentTime;
    struct tm *time_info;
    time(&currentTime);
    time_info=localtime(&currentTime);
    strftime(buffer,20,"%d-%m-%Y",time_info);
    ctime=buffer;
    return;
}

void LOG::logTime(string &ctime)
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

void LOG::__init()
{
    try
    {
        stringstream ss;
        if(isDateWise==true)
        {
                ss<<fileName<<"_"<<date<<".log";
        }
        else
        {
                ss<<fileName<<".log";
        }
        this->fileName=ss.str();
    }
    catch(std::exception &e)
    {
        throw runtime_error(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }
}

string LOG::severityLevel(SEVERITY s)
{
    switch(s)
    {
        case LOG::INFO: return "INFO";
        case LOG::WARNING : return "WARNING";
        case LOG::ERROR : return "ERROR";
        case LOG::CRITICAL : return "CRITICAL";

        default : return "INFO";
    }
}

string LOG::formatter(string &str, va_list args)
{
    char buffer[FMT_BUFFER_SIZE];
    vsnprintf (buffer,FMT_BUFFER_SIZE,str.c_str(), args);
    return string(buffer);
}

inline void LOG::writeToConsole(SEVERITY s,string data)
{
    cout<<"\033[" << s << "m"<<data<<"\n";
    return;
}


void LOG::color(SEVERITY s,string str)
{
    if(colouring==true)
    {
        cout<<"\033[" << s << "m"<<str;
        cout<<"\033[" << 37 << "m";
        cout<<"\033[" << 40 << "m";
    }
    else
    {
        cout<<str;
    }
    return;
}


void LOG::write(SEVERITY s,string str, ...)
{
    try
    {
        out.open(fileName,ios::out | ios::app);
        va_list vargs;
        va_start (vargs, str);

        string lTime;
        logTime(lTime);
        stringstream ss; ss<<"[ "<<lTime<<" ]  [ "<<this->severityLevel(s)<<" ]\t"<<this->formatter(str,vargs)<<"\n";

        out<<ss.str();
        out.close();

        va_end (vargs);

        if(consoleLogging==true)
            color(s,ss.str());
    }
    catch(std::ifstream::failure e)
    {
        throw ifstream(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }
    catch(std::exception &e)
    {
        throw runtime_error(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }
    return ;
}

