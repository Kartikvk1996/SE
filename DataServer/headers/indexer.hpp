/*
@Author : kartik v k
The following class indexes the URL checksum with generated urlId
*/

#ifndef _STDC_H
#define _STDC_H
#include<bits/stdc++.h>
using namespace std;
#endif // _STDC

#ifndef _FORMATTER_H
#define _FORMATTER_H
#include"../../common/headers/formatter.hpp"
#endif // _FORMATTER_H

#include<boost/crc.hpp>
#define URLLENGTH 512

namespace __INDEXER__
{
    struct data
    {
        unsigned long int chksum;       // Stores the checksum of url
        unsigned long int urlId;
        char url[URLLENGTH];
    };

    class IndexException :  public exception
    {
    private:
        string errmsg;
    public:
        IndexException(string emsg):errmsg(emsg) {}

    public:
        const char* what() const throw()
        {
            return errmsg.c_str();
        }
    };

    class Indexer
    {
    private:
        string iFile;
        int dSize=0;
        std::mutex mergeLock; // to prevent re-opening file ptr to new merged file at the same time of other threads reading old file
        std::mutex searchLock;
        FILE *fp=NULL;
        map<unsigned long int,struct data*> tmpStore;  // To store new insert temporarily and perform merge on main file
        int batchLimit=0;
        int counter=0;
    public:
        Indexer(string indFile,int bLimit=1000)
        {
            iFile=indFile;
            counter=0;
            dSize=sizeof(struct data);
            batchLimit=bLimit;
            __init();
        }

        ~Indexer()
        {
            fclose(fp);
        }

    private:
        void __init();
        signed long int searchUrlId(string &,const unsigned long int);
        void mergeWithFile();
        unsigned long int generateChecksum(const string &);

    public:
        unsigned long int generateChecksum(string &);
        void indexUrl(string &, const unsigned long int &);
        signed long int isIndexed(string &);
        void shutdown();
    };
};

