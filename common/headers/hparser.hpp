#define _HTTPPARSER_H

#ifndef _STDC_H
#define _STDC_H
#include<bits/stdc++.h>
#endif // _STDC

#ifndef _FORMATTER_H
#include"formatter.hpp"
#endif // _FORMATTER_H


using namespace std;

namespace __HTTP_PARSER__
{
    class HttpParser
    {
    public:
        map<string,string> POST;
        map<string,string> GET;
        map<string,string> HEADERS;
        map<string,string>::iterator it;

        string method;
        string filePath;


    private:
        string parsableData;

    public:
        HttpParser(string inputData)
        {
            this->parsableData=inputData;
            parseData();
        }

        void parseData();
        void reParseData(string);
        string _GET(string);
        string _POST(string);
        bool issetGET(string);
        bool issetPOST(string);
    };
}
