#include"../headers/indexer.hpp"

using Indexer=__INDEXER__::Indexer;
using IndexException=__INDEXER__::IndexException;


void Indexer::__init()
{
    fp=fopen(iFile.c_str(),"r");
    if(fp==NULL)
    {
        throw IndexException(Formatter()<<"[ "<<__func__<<" ]\tFailed to open file errno : "<<errno<<"\tline : "<<__LINE__<<"\n");
    }
}

void Indexer::indexUrl(string &url, const unsigned long int& uId)
{
    mergeLock.lock();
    struct data *ptr=(struct data*)malloc(sizeof(struct data));
    if(ptr==NULL)
    {
        if(errno==ENOMEM)
            throw runtime_error(Formatter()<<"[ "<<__func__<<" ]\tMalloc Failed ( OUT_OF_MEMORY )"<<"\tline : "<<__LINE__<<"\n");
        else
            throw runtime_error(Formatter()<<"[ "<<__func__<<" ]\tMalloc Failed to allocate memory"<<"\tline : "<<__LINE__<<"\n");
    }

    ptr->chksum=generateChecksum(url);
    ptr->urlId=uId;
    strcpy(ptr->url,url.c_str());
    tmpStore[ptr->chksum]=ptr;

    counter++;
    mergeLock.unlock();

    if(counter>=batchLimit)
    {
        try{
            mergeWithFile();
        }
        catch(__INDEXER__::IndexException &e)
        {
            throw IndexException(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
        }
    }
    return;
}

void Indexer::mergeWithFile()
{
    mergeLock.lock();
    FILE *fw=fopen("temp.dat","w");
    FILE *fr=fopen(iFile.c_str(),"r");
    struct data fData;
    struct data *mData=NULL;
    auto mIttr=tmpStore.begin();

    if(fr==NULL)
    {
        throw IndexException(Formatter()<<"[ "<<__func__<<" ]\tFailed to open file errno : "<<errno<<"\tline : "<<__LINE__<<"\n");
    }

    fseek(fr,0,SEEK_END);
    unsigned long int n1=(ftell(fr)/dSize);
    rewind(fr);
    unsigned long int i=0;
    bool read=true;
    while(i<n1 && mIttr!=tmpStore.end())
    {
        if(read==true)
        {
            fread(&fData,dSize,1,fr);
            read=false;
        }

        mData=mIttr->second;
        if(fData.chksum<mData->chksum)
        {
            //cout<<fData.urlId<<"\t"<<fData.chksum<<"\n";
            fwrite(&fData,dSize,1,fw);
            read=true;
            i++;
        }
        else
        {
            //cout<<mData->urlId<<"\t"<<mData->chksum<<" cache\n";
            fwrite(mData,dSize,1,fw);
            free(mData);
            mIttr++;
        }
    }

    while(i<n1)
    {
        fread(&fData,dSize,1,fr);
        //cout<<fData.urlId<<"\t"<<fData.chksum<<"\n";
        fwrite(&fData,dSize,1,fw);
        i++;
    }

    while(mIttr!=tmpStore.end())
    {
        mData=mIttr->second;
        //cout<<mData->urlId<<"\t"<<mData->chksum<<" cache\n";
        fwrite(mData,dSize,1,fw);
        free(mData);
        mIttr++;
    }
    counter=0;
    mergeLock.unlock();
    tmpStore.clear();

    searchLock.lock();
    fclose(fp);
    remove(iFile.c_str());
    rename("temp.dat",iFile.c_str());
    fp=fopen(iFile.c_str(),"r");
    searchLock.unlock();

    return;
}

signed long int Indexer::searchUrlId(string &url,unsigned long int chksum)
{

    // lookup in store cache
    auto ittr=tmpStore.find(chksum);
    if(ittr!=tmpStore.end())
    {
        struct data *ptr=(*ittr).second;
        if(ptr->url==url)
        {
            return ptr->urlId;
        }
    }


    unsigned long int urlId=-1;
    struct data *tmp=(struct data *)malloc(sizeof(struct data));
    struct data *prev=(struct data *)malloc(sizeof(struct data));

    if(tmp==NULL || prev==NULL)
    {
        if(errno==ENOMEM)
            throw runtime_error(Formatter()<<"[ "<<__func__<<" ]\tMalloc Failed ( OUT_OF_MEMORY )"<<"\tline : "<<__LINE__<<"\n");
        else
            throw runtime_error(Formatter()<<"[ "<<__func__<<" ]\tMalloc Failed to allocate memory"<<"\tline : "<<__LINE__<<"\n");
    }

    long int start=0;

    fseek(fp,0,SEEK_END);
    long int stop=ftell(fp)/sizeof(struct data);
    long int mid;
    while(start<=stop)
    {
        mid=((start+stop)/2);
        fseek(fp,(mid-1)*sizeof(struct data),SEEK_SET);
        fread(prev,sizeof(struct data),1,fp);
        fread(tmp,sizeof(struct data),1,fp);

        if((mid==0 || tmp->chksum > prev->chksum) && tmp->chksum==chksum)
        {
            urlId=tmp->urlId;
            break;
        }
        else if(tmp->chksum<chksum)
        {
            start=mid+1;
        }
        else
        {
            stop=mid-1;
        }
    }

    while(tmp->chksum==chksum && !feof(fp))
    {
        if(tmp->url==url)
        {
            unsigned long int x= tmp->urlId;
            free(tmp);free(prev);
            return x;
        }
        else
        {
            fread(tmp,sizeof(struct data),1,fp);
        }
    }
    free(tmp);free(prev);
    return -1;

}

signed long int Indexer::isIndexed(string &url)
{
    searchLock.lock();
    return searchUrlId(url,generateChecksum(url));
    searchLock.unlock();
}

inline unsigned long int Indexer::generateChecksum(string &url)
{
    boost::crc_32_type result;
    result.process_bytes(url.data(), url.length());
    return result.checksum();
}

void Indexer::shutdown()
{
    try
    {
        mergeWithFile();
    }
    catch(__INDEXER__::IndexException &e)
    {
        throw IndexException(Formatter()<<e.what()<<"Exception Caught : "<<__func__<<"\tline : "<<__LINE__<<"\n");
    }
}
