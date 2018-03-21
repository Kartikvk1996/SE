#include "server.hpp"

/* a workaround to the ugly thread library of C++. */
void thread_piece(ReqHandler *handler, Socket *sock) {
	handler->handle(sock);
}

Server::Server(ServerSocket *serverSock) {
	this->server = serverSock;
}

Server::Server(ReqHandler *handler) {
	server = new ServerSocket(0, MAX_CONNS);
	this->handler = handler;
}

Server::Server(ushort port, ReqHandler *handler) {
	server = new ServerSocket(port, MAX_CONNS);
	this->handler = handler;
}

void Server::run() {
	while(true) {
		Socket *sock = server->acceptConn();
		thread(thread_piece, handler, sock).detach();
		logger.ilog("accepted a connection on thread");
	}
}

string Server::getHost() {
	return server->getHost();
}

ushort Server::getPort() {
	return server->getPort();
}
