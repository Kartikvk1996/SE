
#define SEVERITY unsigned short int
#define FMT_BUFFER_SIZE 65535

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

    private:
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
        return ;
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
        stringstream ss;
        if(isDateWise==true)
        {
            #ifdef __WIN32
                ss<<fileName<<"_"<<date<<".log";
            #endif // __WIN32
                ss<<fileName<<"_"<<date<<".log";
        }
        else
        {
            #ifdef __WIN32
                ss<<fileName<<".log";
            #endif // __WIN32
                ss<<fileName<<".log";
        }
        this->fileName=ss.str();
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

        return ;
    }


}

