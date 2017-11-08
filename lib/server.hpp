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

#include <thread>
#include "socket.hpp"

using namespace std;

#define MAX_CONNS ((ushort)10)
typedef void (*socket_handler)(Socket *sock);

class Server {
	
private:
	ServerSocket *server;
	socket_handler handler;
	
public:
	Server(ServerSocket *serverSock) {
		this->server = serverSock;
	}
	
	Server(string host, ushort port, socket_handler handler) {
		server = new ServerSocket(host, port, MAX_CONNS);
		this->handler = handler;
	}

	void run() {
		while(true) {
			Socket *sock = server->acceptConn();
			thread t(handler, sock);
			dprintf("accepted a connection on thread\n");
			t.join();
		}
	}
};
