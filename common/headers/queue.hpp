#ifndef _STDC_H
#define _STDC_H
#include<bits/stdc++.h>
#endif // _STDC

#ifndef _FORMATTER_H
#include"formatter.hpp"
#endif // _FORMATTER_H


#ifndef _CONFIGURE_H
#include"../../CRSMaster/headers/configure.hpp"
#endif // _CONFIGURE_H


#ifndef _QUEUE_H
#define _QUEUE_H

#ifndef _JSON_H
#define _JSON_H
#include"json.hpp"
#endif // _JSON_H

#include"msgcodes.hpp"

using namespace std;
using json = nlohmann::json;

namespace __QUEUE__
{

#define QADD_LINK_SUCC 1
#define WRITE_SUCC 2
#define MAX_QLIMIT_EXCEED -1
#define LESS_QSPACE -2
#define NO_QSPACE -3

    class QueueException :  public exception
    {
    private:
        string errmsg;
    public:
        QueueException(string emsg):errmsg(emsg) {}

    public:
        const char* what() const throw()
        {
            return errmsg.c_str();
        }
    };


    // Stores the information of each link
    class data
    {
    public:
        unsigned long urlId;
        unsigned long checksum;
        double pgrank;
        unsigned int iLinks;
        unsigned int oLinks;
        string url;
        unsigned int pageSize;
    };


    // user-defined comparator function to group the links in ascending order
    struct compare : public std::binary_function<data, data, bool> {
        bool operator()(const data *lhs, const data *rhs) const
        {
            return !(lhs->urlId == rhs->urlId) && lhs->url<rhs->url;
        }
    };


    class Queue
    {

    private:
        set<data *,compare> que;
        set<data *>::iterator it;
        unsigned long maxSize;

    public:
        unsigned long currQueueSize;


        Queue(unsigned int maxQSize)
        {
            currQueueSize=0;
            maxSize=maxQSize;

        }

        int addLinks(json);
        int addLinks(unsigned long,string,unsigned short,unsigned int);
        json getLinks(unsigned int);
        unsigned long getNoOfLinks();
        int writeInstance();
        int readInstance();
        string searchQueue(string , bool );
        string statusInfo(bool ,unsigned long int);
    };
}




#endif // _QUEUE_



