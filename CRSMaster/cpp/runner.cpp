/*  Author  :   kartik v kalaghatagi
    Date    :   29-10-2017
*/

#pragma once

#include<memory.h>
#include<execinfo.h>

#include"../headers/master.hpp"
#include"../headers/http.hpp"

using namespace std;


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
        CMS *m=new CMS();

        stringstream port;port<<CONFIG::HTTPSERVER_PORT;
        HttpServer *hs = new HttpServer(CONFIG::HTTPSERVER_IPADDRESS,port.str(),CONFIG::HTTPSERVER_THREADPOOLSIZE,m);

        thread worker (&CMS::listenToCrawlers,m);  // listen to crawler
        thread server (&HttpServer::start,hs);

        server.join();
        worker.join();

        delete m;
        delete hs;
    }
    catch(std::exception &e)
    {
        cout<<"Exception caught"<<e.what();
    }
    return 0;
}
