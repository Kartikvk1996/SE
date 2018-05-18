#ifndef _STDC_H
#define _STDC_H
#include<bits/stdc++.h>
using namespace std;
#endif // _STDC>

#include<memory.h>
#include<execinfo.h>


#include"../headers/dataserver.hpp"
#include"../headers/http.hpp"

void handler(int sig) {
  void *array[100];
  size_t size;

  // get void*'s for all entries on the stack
  size = backtrace(array, 100);

  // print out all the frames to stderr
  fprintf(stderr, "Error: signal %d:\n", sig);
  backtrace_symbols_fd(array, size, STDERR_FILENO);
  exit(EXIT_FAILURE);
}

int main()
{
    signal(SIGSEGV, handler);
    try
    {
        DataServer *d=new DataServer(CONFIG::DATASERVER_IPADDRESS,CONFIG::DATASERVER_PORT,CONFIG::MAXCONNECTION);

        stringstream port;port<<CONFIG::HTTPSERVER_PORT;
        HttpServer *hs = new HttpServer(CONFIG::HTTPSERVER_IPADDRESS,port.str(),CONFIG::HTTPSERVER_THREADPOOLSIZE,d);

        thread worker (&DataServer::listener,d);
        thread server (&HttpServer::start,hs);

        server.join();
        worker.join();

        delete d;
        delete hs;
    }
    catch(std::exception &e)
    {
        cout<<"Exception caught"<<e.what();
    }
    return 0;
}

