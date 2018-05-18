#include"../headers/storageengine.hpp"

using StorageEngine=__STORAGE__::StorageEngine;
using StoreException=__STORAGE__::StorageException;

void StorageEngine::__init()
{
    ifstream in;
    in.open("info.dat");
    string send((std::istreambuf_iterator<char>(in) ),(std::istreambuf_iterator<char>()));
    dsStatus=json::parse(send);
    totLinks=dsStatus["totallinks"];

}

unsigned long int StorageEngine::addUrl(unsigned long int chksum,string url)
{
    try
    {
        return writer(chksum,url);
    }
    catch(StorageException &e)
    {
        throw StorageException(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }
}

json StorageEngine::getUrlsInfo(vector<unsigned long int> &urlIds)
{
    json data;
    struct linkInfo *lData;
    for(auto ittr=urlIds.begin();ittr!=urlIds.end();ittr++)
    {
        try
        {
            //if(*ittr>0 && *ittr<=totLinks) and sort the cache by list ----todo
            {
                lData=reader(*ittr);
                stringstream ss;ss<<lData->urlId;
                string urlId=ss.str();
                data[urlId]["pgrank"]=lData->pgrank;
                data[urlId]["lastupdated"]=lData->lastCrawled;
                data[urlId]["url"]=lData->url;
                data[urlId]["ilinks"]=lData->iLinks;
                data[urlId]["olinks"]=lData->oLinks;
                data[urlId]["pagesize"]=lData->pageSize;
                data[urlId]["abstract"]=lData->abstract;
                data[urlId]["title"]=lData->title;
            }
        }
        catch(StorageException &e)
        {
            throw StorageException(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
        }
        catch(std::exception &e)
        {
            throw runtime_error(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
        }
    }
    return data;
}

unsigned long int StorageEngine::writer(unsigned long int chksum,string & url)
{
    std::lock_guard<std::mutex> lock(writeLock);


    int writeFd=getFileDescriptor(WRITE,0); // get write FD;

    unsigned long urlId=lseek64(writeFd,0,SEEK_END);
    struct linkInfo lData;
    lData.checksum=chksum;
    lData.iLinks=0;
    lData.oLinks=0;
    lData.pgrank=1;
    lData.pageSize=0;

    totLinks++;
    lData.urlId=totLinks;

    lData.lastCrawled=(unsigned long )std::time(nullptr);
    lData.changeRate=255;

    strcpy(lData.url,url.c_str());
    lData.abstract[0]='\0';
    lData.title[0]='\0';

    cout<<lData.url<<"-"<<lData.urlId<<"-"<<lData.abstract<<"-"<<lData.title<<"-"<<lData.changeRate<<"-"<<lData.pageSize<<"-"<<lData.pgrank<<"-"<<lData.lastCrawled<<"\n";


    short status=write(writeFd,&lData,linkInfoSize);
    if(status<0)
    {
        totLinks--;
        throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tWriter failed to write the block\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
    }
    else
    {
        return totLinks;
    }

}

void StorageEngine::updater(unsigned long int urlId,double pgrank=1.0,unsigned int ilinks=0,unsigned int olinks=0,unsigned int pageSize=0,unsigned char lastCrawled=0,string title="",string abstract="") //update
{
    struct linkInfo *l=reader(urlId);
    unsigned long offset=(urlId-1)*sizeof(struct linkInfo);

    if(pgrank!=1.0)
        l->pgrank=((l->pgrank)+pgrank);
    if(ilinks!=0)
        l->iLinks=((l->iLinks)+ilinks);
    if(olinks!=0)
        l->iLinks=((l->oLinks)+olinks);
    if(pageSize!=0)
        l->pageSize=pageSize;
    if(lastCrawled!=0)
        l->lastCrawled=lastCrawled;
    if(title!="")
        strcpy(l->title,title.substr(0,TITLE).c_str());
    if(abstract!="")
        strcpy(l->abstract,abstract.substr(0,ABSTRACT).c_str());


    std::lock_guard<std::mutex> lock(writeLock);

    int writeFd=getFileDescriptor(WRITE,urlId);


    struct flock wlock;
    wlock.l_type=F_WRLCK;
    wlock.l_whence=SEEK_SET;
    wlock.l_start=offset;
    wlock.l_len=linkInfoSize;

    short status=fcntl(writeFd,F_SETLKW,&wlock);
    if(status<-1)
    {
        if(errno==EINTR)
            throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tWriter lock wait interrupted\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
        else
            throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tWriter failed to lock the region\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
    }

    lseek64(writeFd,offset,SEEK_SET);
    status=write(writeFd,l,linkInfoSize);

    if(status<0)
    {
        throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tReader failed to read the block\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
    }

    wlock.l_type=F_UNLCK;
    status=fcntl(writeFd,F_SETLKW,&wlock);

    if(status<-1)
    {
        if(errno==EINTR)
        {
            if(errno==EINTR)
                throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tWriter lock wait interrupted\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
            else
                throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tWriter failed to lock the region\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
        }
    }
    return;
}


struct linkInfo* StorageEngine::reader(unsigned long int urlId)
{
    struct linkInfo *lData=NULL;
    auto ittr=lkpCache.find(urlId);
    if(ittr!=lkpCache.end())
    {
        auto res=ittr->second;
        if(res.first<UINT_MAX)
            res.first+=1;
        lData=res.second;
        //cout<<"Cache hit Data read\t"<<lData->urlId<<" "<<lData->pgrank<<" "<<lData->url<<" "<<lData->lastCrawled<<"\n";
        return res.second;
    }

    std::lock_guard<std::mutex> lock(seekLock);

    unsigned long int offset=(urlId-1)*linkInfoSize;

    int readFd=getFileDescriptor(READ,urlId);// get file descriptor
    // lock the region


    struct flock rlock;
    rlock.l_type=F_RDLCK;
    rlock.l_whence=SEEK_SET;
    rlock.l_start=offset;
    rlock.l_len=linkInfoSize;

    short status=fcntl(readFd,F_SETLKW,&rlock);
    if(status<-1)
    {
        if(errno==EINTR)
            throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tReader lock wait interrupted\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
        else
            throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tReader failed to lock the region\terrno : "<<errno<<" line : "<<__LINE__<<"\n");

    }

    lData=(struct linkInfo *)malloc(linkInfoSize);
    if(lData==NULL)
    {
        if(errno==ENOMEM)
            throw runtime_error(Formatter()<<"[ "<<__func__<<" ]\tMalloc Failed ( OUT_OF_MEMORY )"<<"\tline : "<<__LINE__<<"\n");
        else
            throw runtime_error(Formatter()<<"[ "<<__func__<<" ]\tMalloc Failed to allocate memory"<<"\tline : "<<__LINE__<<"\n");
    }
    lseek64(readFd,offset,SEEK_SET);
    status=read(readFd,lData,linkInfoSize);

    if(status<0)
    {
        throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tReader failed to read the block\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
    }

    if(lkpCache.size()>lkpMaxCacheSize)
    {
        //cout<<lkpMaxCacheSize<<" : max size\n";
        vector<map<unsigned long int,pair<unsigned int,struct linkInfo*>>::iterator> v;

        for(auto it=lkpCache.begin();it!=lkpCache.end();it++)
        {
            if((*it).second.first==1)
                v.push_back(it);
        }

        for(auto it=v.begin();it!=v.end();it++)
        {
            free((*(*it)).second.second);
            lkpCache.erase(*it);
        }
    }

    lkpCache[urlId]=make_pair(1,lData);
    rlock.l_type=F_UNLCK;
    status=fcntl(readFd,F_SETLKW,&rlock);

    if(status<-1)
    {
        if(errno==EINTR)
        {
            if(errno==EINTR)
                throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tWriter lock wait interrupted\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
            else
                throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tWriter failed to lock the region\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
        }
    }
    return lData;
};


int StorageEngine::getFileDescriptor(short type,unsigned long int urlId)
{
    if(type==WRITE)
    {
        int fname;
        if(urlId==0)
        {
            fname=(totLinks+1)/splitFactor;
        }
        else
        {
            fname=urlId/splitFactor;
        }
        auto ittr=writeFileDescriptors.find(fname);
        if(ittr!=writeFileDescriptors.end())
        {
            return ittr->second;
        }
        stringstream ss;ss<<"data/urlinfo/"<<fname<<".dat";
        string file=ss.str();

        int fd=open(file.c_str(),O_CREAT | O_WRONLY | O_APPEND);
        if(fd<0)
        {
            throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tFailed to open file\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
        }
        writeFileDescriptors[fname]=fd;
        return fd;
    }
    else if(type==READ)
    {

        int fname=urlId/splitFactor;
        auto ittr=readFileDescriptors.find(fname);
        if(ittr!=readFileDescriptors.end())
        {
            return ittr->second;
        }
        else
        {
            stringstream ss;ss<<"data/urlinfo/"<<fname<<".dat";
            string file=ss.str();
            int fd=open(file.c_str(),O_RDONLY);
            if(fd<0)
            {
                throw StorageException(Formatter()<<"[ "<<__func__<<" ]\tFailed to open file\terrno : "<<errno<<" line : "<<__LINE__<<"\n");
            }
            readFileDescriptors[fname]=fd;
            return fd;
        }
    }
    else
    {
        for(auto ittr=readFileDescriptors.begin();ittr!=readFileDescriptors.end();ittr++)
        {
            int fd=ittr->second;
            close(fd);
        }

        for(auto ittr=writeFileDescriptors.begin();ittr!=writeFileDescriptors.end();ittr++)
        {
            int fd=ittr->second;
            close(fd);
        }
        return -1;
    }
}

void StorageEngine::shutdown()
{
    dsStatus["totallinks"]=totLinks;
    stateLock.lock();
    ofstream out;
    out.open("info.dat",ios::out);
    out<<dsStatus.dump(1);
    stateLock.unlock();
    getFileDescriptor(CLOSE ,0);
    return;
}

unsigned int StorageEngine::getTotalLinks()
{
    return totLinks;
}

struct linkInfo* StorageEngine::getUrlToCrawl(unsigned long int urlId)
{
    struct linkInfo *tmp=reader(urlId);
    unsigned long time=std::time(nullptr);

    unsigned long diff=tmp->lastCrawled-time;
    unsigned char rate= tmp->changeRate;
    rate=rate/(60*60);   // convert to hours

    double prob=(rate)/255;
    double d=1.0-prob;
    d=d*10;
    d=pow(2,d);
    if(diff>=d)
    {
        return tmp;
    }
    else
    {
        return NULL;
    }
}
