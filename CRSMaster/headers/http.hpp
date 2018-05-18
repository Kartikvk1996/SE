#define MAXEVENTS 128

#define _HTTP_H

#ifndef _CONNECTION_H
#include<netinet/in.h>
#include<fcntl.h>
#include<sys/stat.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<unistd.h>
#include<string.h>
#include<arpa/inet.h>
#include<netdb.h>
#endif // _CONNECTION_H

#include <sys/epoll.h>

#include"factory.hpp"



class HttpServer
{
private:
    struct addrinfo serverAddress, *s=NULL;
    int status, serverFd,eventFd;
    string ipAddress;
    string port;

    struct epoll_event sevent;              // main event
    struct epoll_event *events=NULL;        // list of events that will be stored when multiple clients generate requests
    Factory *f=NULL;
    bool stopServer=false;
    CMS *obj=NULL;
    int maxThreads;


public:
    HttpServer(string ip,string pt,int mThds,CMS *o)
    {
        ipAddress=ip;
        port=pt;
        status=serverFd=0;
        maxThreads=mThds;
        obj=o;
    }

    ~HttpServer()
    {
        delete f;
        if(events!=NULL)
            free(events);

    }

private:
    int createSocket();
    void createEventListener();
    void serveClients();

public:
    void start();
};

void HttpServer::start()
{
    if(createSocket()<0)
        assert("failed to create socket");
    cout<<serverFd;
    createEventListener();

    f=new Factory(&eventFd,&serverFd,&sevent,&events,&stopServer,obj);
    serveClients();
}



int HttpServer::createSocket()
{
    memset(&serverAddress,0,sizeof(struct addrinfo));
    serverAddress.ai_family=AF_INET;
    serverAddress.ai_socktype=SOCK_STREAM;
    serverAddress.ai_flags=AI_PASSIVE;

    status=getaddrinfo(NULL,port.c_str(),&serverAddress,&s);

    if(status!=0)
    {
        cout<<gai_strerror (status);
        return -1;
    }

    serverFd=socket(s->ai_family,s->ai_socktype,s->ai_protocol);
    if(serverFd==-1)
    {
        return -1;
    }

    int reuse=1;
    setsockopt(serverFd, SOL_SOCKET, SO_REUSEPORT, &reuse, sizeof(reuse));

    status=bind(serverFd,s->ai_addr,s->ai_addrlen);
    if(status==-1)
    {
        return -1;
    }

    freeaddrinfo(s);

    int flags = fcntl (serverFd, F_GETFL, 0);
    flags |= O_NONBLOCK;
    fcntl (serverFd, F_SETFL, flags);


    if(listen(serverFd,SOMAXCONN)<0)
        return -1;

    return serverFd;

}

void HttpServer::createEventListener()
{
    eventFd = epoll_create1 (0);  // create epoll instance
    if (eventFd == -1)
    {
      perror ("epoll_create");
      abort ();
    }

    sevent.data.fd = serverFd;
    sevent.events = EPOLLIN | EPOLLET;
    status = epoll_ctl (eventFd, EPOLL_CTL_ADD, serverFd, &sevent);
    if (status == -1)
    {
      perror ("epoll_ctl");
      abort ();
    }

    events = (epoll_event*) calloc (MAXEVENTS, sizeof(sevent));
    assert(events!=NULL);
}

void HttpServer::serveClients()
{
    do
    {
        int nevents=epoll_wait(eventFd,events,MAXEVENTS,-1);
        f->handleEvents(0,nevents);

    }while(stopServer==false);
    close(serverFd);
    close(eventFd);
}
