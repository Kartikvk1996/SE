#ifndef _STDC_H
#define _STDC_H
#include<bits/stdc++.h>
using namespace std;
#endif // _STDC

#ifndef _FORMATTER_H
#define _FORMATTER_H
#include"../../common/headers/formatter.hpp"
#endif // _FORMATTER_H

#ifndef _JSON_H
#define _JSON_H
#include"../../common/headers/json.hpp"
using json=nlohmann::json;
#endif // _JSON_H


#include<sys/types.h>
#include<unistd.h>
#include<errno.h>
#include<fcntl.h>
#include"linkInfo.hpp"

#define READ 0
#define WRITE 1
#define CLOSE 2


namespace __STORAGE__
{

    class StorageException :  public exception
    {
    private:
        string errmsg;
    public:
        StorageException(string emsg):errmsg(emsg) {}

    public:
        const char* what() const throw()
        {
            return errmsg.c_str();
        }
    };

    class StorageEngine
    {
    private:
        unsigned int lkpMaxCacheSize=0;
        unsigned int splitFactor=0;
        short linkInfoSize=0;
        unsigned long int totLinks=0;
        mutex writeLock;
        mutex seekLock;
        mutex stateLock;
        json dsStatus;


        map<unsigned long int,pair<unsigned int,struct linkInfo*>> lkpCache; //<urlid,<hits,linkinfo>>
        map<int,int> writeFileDescriptors;
        map<int,int> readFileDescriptors;



    public:
        StorageEngine(int sFactor=10000,int lkmc=1000)
        {
            splitFactor=sFactor;
            lkpMaxCacheSize=lkmc;
            linkInfoSize=sizeof(struct linkInfo);
            __init();
        }

        unsigned long int addUrl(unsigned long int,string);
        json getUrlsInfo(vector<unsigned long int> &);
        struct linkInfo* getUrlToCrawl(unsigned long int);
        unsigned int getTotalLinks();
        void shutdown();
    private:
        struct linkInfo* reader(unsigned long int);
        unsigned long int writer(unsigned long int ,string&);
        void updater(unsigned long int ,double ,unsigned int ,unsigned int ,unsigned int ,unsigned char,string,string );
        void __init();
        int getFileDescriptor(short, unsigned long int);
    };
}

