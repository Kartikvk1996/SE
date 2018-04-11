
using json = nlohmann::json;

class data
{
public:
    unsigned long urlId;
    string url;
    unsigned short depth;
    unsigned int pageSize;
};



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
    LOG *log;

public:
    unsigned long currQueueSize;


    Queue(unsigned int maxQSize)
    {
        currQueueSize=0;
        maxSize=maxQSize;
        log=new LOG(CONFIG::QUEUE_LOG_NAME,CONFIG::QUEUE_LOGGING_DATEWISE,CONFIG::QUEUE_CONSOLE_LOGGING,CONFIG::QUEUE_CONSOLE_COLOURING);
    }


    bool addLinks(json);
    bool addLinks(unsigned long,string,unsigned short,unsigned int);
    json getLinks(unsigned int);
    unsigned long getNoOfLinks();
    bool writeInstance();
    bool readInstance();
    string searchQueue(string , bool );
    string statusInfo(bool ,unsigned long int);

};

bool Queue::addLinks(json urlData)
{
    json urlInfo;
    unsigned long n=urlData["nooflinks"];
    if(n<0)
    {
        log->write(LOG::WARNING,"[%s]\tNo links to add",__func__);
        return false;
    }

    if(n>(maxSize-currQueueSize))
    {
        log->write(LOG::WARNING,"[%s]\tNo space to add %uld links , available is %uld",__func__,n,maxSize-currQueueSize);
        return false;
    }

    data *d=new data();
    for(unsigned long i=0;i<n;i++)
    {
        urlInfo=urlData[i];
        d->urlId=urlInfo["urlid"];
        d->url=urlInfo["url"];
        d->depth=urlInfo["depth"];
        d->pageSize=urlInfo["length"];
        if(d->depth>0)   // remove if depth is less than or equals to 0
        {
            que.insert(d);
            currQueueSize++;
        }
    }
    log->write(LOG::INFO,"[%s]\t%ul links added to queue",__func__,n);
    return true;
}


bool Queue::addLinks(unsigned long urlid,string url,unsigned short depth=1,unsigned int length=0)
{
    if(maxSize-currQueueSize<=0)
    {
        log->write(LOG::WARNING,"[%s]\tNo space to add %ul links , available is %ul",__func__,1,maxSize-currQueueSize);
        return false;
    }

    data *d=new data();
    d->urlId=urlid;
    d->url=url;
    d->depth=depth;
    d->pageSize=length;
    que.insert(d);
    currQueueSize=que.size();
    log->write(LOG::INFO,"[%s]\t%uld links added to queue",__func__,1);
    return true;
}

json Queue::getLinks(unsigned int cnt)
{
    auto q=que.begin(); // To iterate through queue
    json urlInfo,urlData;
    unsigned int counter=0;
    urlInfo["mode"]=RESPONSE;
    if(currQueueSize>0)
    {
        it=que.begin();
        urlInfo["type"]=CRS_SND_NLINKS_SUCC;
        for(q=que.begin();q!=que.end();q++)
        {
            urlData["urlid"]=(*q)->urlId;
            urlData["url"]=(*q)->url;
            urlData["depth"]=(*q)->depth;
            urlData["length"]=(*q)->pageSize;
            urlInfo["Data"][counter]=urlData;
            currQueueSize--;
            counter++;
            cnt--;
            delete *q;
            if(cnt<=0)
            {
                break;
            }
        }
        que.erase(que.begin(),q);
        urlInfo["nooflinks"]=counter;
    }
    else
    {
        urlInfo["mode"]=RESPONSE;
        urlInfo["type"]=CRS_SND_NLINKS_ZERO;
        urlInfo["nooflinks"]=counter;
    }
    return urlInfo;
}

bool Queue::writeInstance()
{
    ofstream fp(CONFIG::QUEUE_DATAFILE);
    if(!fp.is_open())
    {
        log->write(LOG::ERROR,"[%s]\tQueue failed to open %s ",__func__,CONFIG::QUEUE_DATAFILE.c_str());
        return false;
    }
    int temp=que.size();
    for(it=que.begin();it!=que.end();it++)
    {
        fp<<(*it)->urlId<<" "<<(*it)->url<<" "<<(*it)->depth<<" "<<(*it)->pageSize<<"\n";
        delete *it;
    }
    log->write(LOG::INFO,"[%s]\t%d links written to %s",__func__,temp,CONFIG::QUEUE_DATAFILE.c_str());
    currQueueSize=0;
    fp.close();
    que.erase(que.begin(),que.end());
    return true;
}

bool Queue::readInstance()
{
    ifstream fp(CONFIG::QUEUE_DATAFILE);
    if(!fp.is_open())
    {
        log->write(LOG::ERROR,"[%s]\tQueue failed to open %s ",__func__,CONFIG::QUEUE_DATAFILE.c_str());
        return false;
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
        d->depth=(unsigned short)std::stoul(token,nullptr,0);
        getline(ss,token,' ');
        d->pageSize=(unsigned int)std::stoul(token,nullptr,0);
        que.insert(d);
    }
    currQueueSize=que.size();
    log->write(LOG::INFO,"[%s]\t%d links loaded from %s",__func__,currQueueSize,CONFIG::QUEUE_DATAFILE.c_str());
    fp.close();
    return true;
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
    sendData["Maximum size"]=this->maxSize;
    sendData["Current size"]=this->currQueueSize;
    if(showlinks)
    {
        for(it=que.begin();it!=que.end();it++)
        {
            urlInfo["urlId"]=(*it)->urlId;
            urlInfo["url"]=(*it)->url;
            urlInfo["depth"]=(*it)->depth;
            sendData["Links"][ittr++]=urlInfo;
            if(ittr>cnt)
            {
                break;
            }
        }
    }
    return sendData.dump(5);
}
