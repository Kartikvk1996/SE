#include"../headers/queue.hpp"


using Queue=__QUEUE__::Queue;
using QueueException=__QUEUE__::QueueException;

int Queue::addLinks(json urlData)
{
    json urlInfo;
    unsigned long n=urlData["nooflinks"];
    if(n<0)
    {
        throw QueueException(Formatter()<<"[Negative value :"<<n<<"] While adding new links"<<"\tline : "<<__LINE__<<"\n");
    }

    if(n>(maxSize-currQueueSize))
    {
        return LESS_QSPACE;
    }

    for(unsigned long i=0;i<n;i++)
    {
        data *d=new data();
        urlInfo=urlData["data"][i];
        d->urlId=urlInfo["urlid"];
        d->url=urlInfo["url"];
        d->checksum=urlInfo["checksum"];
        d->pgrank=urlInfo["pgrank"];
        d->pageSize=urlInfo["pagesize"];
        d->iLinks=urlInfo["ilinks"];
        d->oLinks=urlInfo["olinks"];

            que.insert(d);
            currQueueSize++;

    }

    return n;
}

/*
int Queue::addLinks(unsigned long urlid,string url,unsigned short depth=1,unsigned int length=0)
{
    if(maxSize-currQueueSize<=0)
    {
        return NO_QSPACE;
    }

    data *d=new data();
    d->urlId=urlid;
    d->url=url;
    d->depth=depth;
    d->pageSize=length;
    que.insert(d);
    currQueueSize=que.size();
    //log->write(LOG::INFO,"[%s]\t%uld links added to queue",__func__,1);
    return QADD_LINK_SUCC;
}
*/

json Queue::getLinks(unsigned int cnt)
{
    auto q=que.begin(); // To iterate through queue
    json urlInfo,urlData;
    unsigned int counter=0;
    urlInfo["mode"]=RESPONSE;
    if(currQueueSize>0)
    {
        urlInfo["type"]=SND_NLINKS_SUCC;
        for(q=que.begin();q!=que.end();q++)
        {
            urlData["urlid"]=(*q)->urlId;
            urlData["url"]=(*q)->url;
            urlData["checksum"]=(*q)->checksum;
            urlData["pagesize"]=(*q)->pageSize;
            urlData["ilinks"]=(*q)->iLinks;
            urlData["olinks"]=(*q)->oLinks;
            urlData["pgrank"]=(*q)->pgrank;
            urlInfo["data"][counter]=urlData;
            currQueueSize--;
            counter++;
            cnt--;

            if(cnt<=0)
            {
                break;
            }
        }
        q++;
        for(auto it=que.begin();it!=q;it++)
        {
            delete *it;
        }

        que.erase(que.begin(),q);

        urlInfo["nooflinks"]=counter;
    }
    else
    {
        urlInfo["mode"]=RESPONSE;
        urlInfo["type"]=SND_NLINKS_ZERO;
        urlInfo["nooflinks"]=counter;
    }
    return urlInfo;
}

int Queue::writeInstance()
{
    ofstream fp(CONFIG::QUEUE_DATAFILE,ios::out | ios::trunc);
    if(!fp.is_open())
    {
        throw QueueException(Formatter()<<"[ "<<__func__<<" ]\t"<<"Failed to open "<<CONFIG::QUEUE_DATAFILE<<"\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
    }
    //int temp=que.size();
    for(it=que.begin();it!=que.end();it++)
    {
        fp<<(*it)->urlId<<" "<<(*it)->url<<" "<<(*it)->checksum<<" "<<(*it)->pageSize<<" "<<(*it)->pgrank<<" "<<(*it)->iLinks<<" "<<(*it)->oLinks<<"\n";
        delete *it;
    }
    //log->write(LOG::INFO,"[%s]\t%d links written to %s",__func__,temp,CONFIG::QUEUE_DATAFILE.c_str());
    currQueueSize=0;
    fp.close();
    que.erase(que.begin(),que.end());
    return WRITE_SUCC;
}

int Queue::readInstance()
{
    ifstream fp(CONFIG::QUEUE_DATAFILE);
    if(!fp.is_open())
    {
        throw QueueException(Formatter()<<"[ "<<__func__<<" ]\t"<<"Failed to open "<<CONFIG::QUEUE_DATAFILE<<"\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
    }

    string inpData,token;
    while(getline(fp,inpData))
    {
        stringstream ss(inpData);
        data *d=new data();
        // load data
        getline(ss,token,' ');
        d->urlId=std::stoul(token,nullptr,0);
        getline(ss,token,' ');
        d->url.append(token);
        getline(ss,token,' ');
        d->checksum=(unsigned short)std::stoul(token,nullptr,0);
        getline(ss,token,' ');
        d->pageSize=(unsigned int)std::stoul(token,nullptr,0);
        getline(ss,token,' ');
        d->pgrank=(double)std::stod(token);
        getline(ss,token,' ');
        d->iLinks=(unsigned int)std::stoul(token,nullptr,0);
        que.insert(d);
        d->oLinks=(unsigned int)std::stoul(token,nullptr,0);
        que.insert(d);
    }
    currQueueSize=que.size();
    //log->write(LOG::INFO,"[%s]\t%d links loaded from %s",__func__,currQueueSize,CONFIG::QUEUE_DATAFILE.c_str());
    fp.close();
    return currQueueSize;
}

string Queue::searchQueue(string searchUrl, bool similar=false)
{
    json sendData,urlInfo;
    unsigned long ittr=0;
    if(similar==true)
    {
        searchUrl="(.*)"+searchUrl+"(.*)";
        regex reg(searchUrl);
        for(it=que.begin();it!=que.end();it++)
        {
            if(regex_match((*it)->url,reg))
            {
                urlInfo["urlId"]=(*it)->urlId;
                urlInfo["url"]=(*it)->url;
                sendData["Result"][ittr++]=urlInfo;
            }
        }
        sendData["Count"]=ittr;
    }
    else
    {
        for(it=que.begin();it!=que.end();it++)
        {
           // cout<<(*it)->url<<"\t"<<searchUrl<<"\n";
            if((*it)->url==searchUrl)
            {
                urlInfo["urlId"]=(*it)->urlId;
                urlInfo["url"]=(*it)->url;
                sendData["Result"][ittr++]=urlInfo;
            }
        }
    }
    return sendData.dump(5);
}


string Queue::statusInfo(bool showlinks=false,long unsigned int cnt=100) // first 100 links will be shown , can be override with any value
{
    json sendData,urlInfo;
    unsigned long ittr=0;
    sendData["maximum_size"]=this->maxSize;
    sendData["current_size"]=this->currQueueSize;
    if(showlinks)
    {
        for(it=que.begin();it!=que.end();it++)
        {
            urlInfo["urlId"]=(*it)->urlId;
            urlInfo["url"]=(*it)->url;
            urlInfo["checksum"]=(*it)->checksum;
            urlInfo["pagesize"]=(*it)->pageSize;
            urlInfo["ilinks"]=(*it)->iLinks;
            urlInfo["olinks"]=(*it)->oLinks;
            urlInfo["pgrank"]=(*it)->pgrank;
            sendData["Links"][ittr++]=urlInfo;
            if(ittr>cnt)
            {
                break;
            }
        }
    }
    return sendData.dump(5);
}

unsigned long int Queue::getNoOfLinks()
{
    return currQueueSize;
}
