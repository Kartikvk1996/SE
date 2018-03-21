/*
* file: server.cpp
* 
* contributors: 
*      Madhusoodan M Pataki [8 Nov 2017]
* 
* A wrapper class around sockets for easing some work Server creation
* which listens to the connections and creates a thread for handling
* that socket
*/

#ifndef SERVER_INCLUDED
#define SERVER_INCLUDED

#include <thread>
#include "socket.hpp"
#include "reqhandler.hpp"

using namespace std;

#define MAX_CONNS ((ushort)10)

class Server {
	
private:
	ServerSocket *server;
	ReqHandler *handler;
	
public:
	Server(ServerSocket *serverSock);
	
	Server(ReqHandler *handler);

	Server(ushort port, ReqHandler *handler);

	string getHost();

	ushort getPort();

	void run();
};

#endif
