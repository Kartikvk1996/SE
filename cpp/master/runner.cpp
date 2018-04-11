/*  Author  :   kartik v kalaghatagi
    Date    :   29-10-2017
*/


/*------------------------------------------------------------Libraries----------------------------------------------------------------------*/
#include"headers.hpp"

void handler(int sig) {
  void *array[100];
  size_t size;

  // get void*'s for all entries on the stack
  size = backtrace(array, 10);

  // print out all the frames to stderr
  fprintf(stderr, "Error: signal %d:\n", sig);
  backtrace_symbols_fd(array, size, STDERR_FILENO);
  exit(EXIT_FAILURE);
}


int main()
{
    signal(SIGSEGV, handler);
    CMS *m=new CMS();
    HttpServer *hs = new HttpServer(CONFIG::HTTPSERVER_IPADDRESS,CONFIG::HTTPSERVER_PORT,CONFIG::HTTPSERVER_THREADPOOLSIZE,m);

    thread worker (&CMS::listenToCrawlers,m);  // listen to crawler
    thread server (&HttpServer::runner,hs);

    server.join();
    worker.join();

    delete m;
    delete hs;
    return 0;
}
