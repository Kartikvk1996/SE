#include "server.hpp"

/* a workaround to the ugly thread library of C++. */
void thread_piece(ReqHandler *handler, Socket *sock) {
	handler->handle(sock);
}

Server::Server(ServerSocket *serverSock) {
	this->server = serverSock;
}
	
Server::Server(string host, ushort port, ReqHandler *handler) {
	server = new ServerSocket(host, port, MAX_CONNS);
	this->handler = handler;
}

void Server::run() {
	while(true) {
		Socket *sock = server->acceptConn();
		thread(thread_piece, handler, sock).detach();
		dprintf("accepted a connection on thread\n");
	}
}

string Server::getHost() {
	return server->getHost();
}

ushort Server::getPort() {
	return server->getPort();
}
