#include "server.hpp"

Server::Server(ServerSocket *serverSock) {
	this->server = serverSock;
}
	
Server::Server(string host, ushort port, socket_handler handler) {
	server = new ServerSocket(host, port, MAX_CONNS);
	this->handler = handler;
}

void Server::run() {
	while(true) {
		Socket *sock = server->acceptConn();
		thread(handler, sock).detach();
		dprintf("accepted a connection on thread\n");
	}
}
