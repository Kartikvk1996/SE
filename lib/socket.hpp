/* 
 * file : socket.hpp
 * socket objects used for wrapping up raw-tcp sockets
 *
 * contributors:
 * 	Kartik Kalaghatagi [28 Oct 2017]
 * 	Madhusoodan Pataki [7 Nov 2017]
 */

#ifdef __unix__
	#include<malloc.h>
	#include<netinet/in.h>
	#include<fcntl.h>
	#include<sys/stat.h>
	#include<sys/socket.h>
	#include<sys/types.h>
	#include<unistd.h>
	#include<string.h>
	#include<arpa/inet.h>
	#include<netdb.h>
#elif __WIN32
	#include<windows.h>
	#include<winsock2.h>
	#include<ws2tcpip.h>
	#include<bits/stdc++.h>
#endif

#ifdef __unix__
	typedef int sock_t;
	#define INVALID_SOCKET (-1)
	#define SOCKET_ERROR (-1)
#elif __WIN32
	typedef SOCKET sock_t;
	typedef int socklen_t;
	#define write send
	#define read recv
	#define close closesocket
#endif

#define SOCK_BUFFER_SIZE 65535

#include <iostream>
#include "../include/debug.h"
using namespace std;

class Socket {

protected:
	struct sockaddr_in addr;
	sock_t fd;
	bool alive;

	bool socket_setup();
private:
	string host;
	ushort port;
	char msg[SOCK_BUFFER_SIZE];
#ifdef __WIN32
	static WSADATA wsa;
#endif
	bool sock_connect();

public:
	void set_sock(string host, ushort port) {
		this->host = host;
		this->port = port;
	}
	Socket() { /* default do nothing */ }
	Socket(sock_t fd) {
		this->fd = fd;
	}
	Socket(string host, ushort port) {
		set_sock(host, port);
		if (!(alive = sock_connect())) {
			dprintf("Socket:: failed to connect\n");
		}
	}

	~Socket() {
		closeConn();
	}

	bool writeData(string message);	/* To write data to socket */
	string readData();		/* To read data to socket */
	ushort getPort();		/* To get port number */
	string getHost();		/* To get IP Address */
	void closeConn();		/* To close connection */
	bool isAlive();			/* To check connection liveness */
};


bool Socket :: socket_setup() {

#ifdef __WIN32
	if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
		dprintf("Socket:: WSAStartup failed\n");
		return false;
	}
#endif

	/* Opens the socket connections */
	fd = socket(AF_INET, SOCK_STREAM, 0);

	if (fd == INVALID_SOCKET) {
		dprintf("Socket:: Failed to create socket\n");
		return false;
	}

	/* normal init stuff... [refer setting up sockets in unix] */
	addr.sin_family = AF_INET;
	addr.sin_addr.s_addr = inet_addr(host.c_str());
	addr.sin_port = htons(port);
	return true;
}

/* connects the socket to the server */
bool Socket :: sock_connect() {
	if(!socket_setup())
		return false;
	if(connect(fd, (struct sockaddr*)&addr, sizeof(addr)) == -1) {
		dprintf("Socket:: failed to connect to server\n");
		return false;
	}
	return true;
}

/*  writes 'data' on socket returns true as success, false otherwise. */
bool Socket :: writeData(string data) {
	if (write(fd, data.c_str(), data.length()) > 0) {
		return true;
	} else {
		dprintf("Socket:: Failed to write to socket\n");
		return false;
	}
}

/* reads data from socket and returns it on success, NUll otherwise.
 * this method can provide chunks on socket hence you need to call
 * the read multiple times for getting whole data.
 */
string Socket :: readData() {
	try {
		if (read(fd, msg, SOCK_BUFFER_SIZE) > 0) {
			return string(msg);
		} else {
			dprintf("Socket:: Failed to read from socket\n");
			return "";
		}
	} catch(...){}
}

/* returns the port value on which socket connection is open */
ushort Socket :: getPort() {
	return port;
}

/* returns the Host Address */
string Socket :: getHost() {
	return host;
}

/* closes the socket connection */
void Socket :: closeConn() {
	alive = false;
	close(fd);
}

/* TODO: [Is it what we need] tells whether we are connected? */
bool Socket :: isAlive() {
	return alive;
}


class ServerSocket : public Socket {

private:
	ushort maxConns;
	sockaddr_in c_addr;

	bool sock_connect();
public:
	ServerSocket(string host, ushort port, ushort maxConns): Socket() {
		set_sock(host, port);
		this->maxConns = maxConns;
		if (!(alive = ServerSocket::sock_connect())) {
			dprintf("ServerSocket:: Failed to start server\n");
		}
	}

	Socket* acceptConn();
};

/* returns the socket connected to a client */
Socket* ServerSocket :: acceptConn() {
	socklen_t cli_len = sizeof(c_addr);
	sock_t nfd = accept(fd, (struct sockaddr*)&c_addr, &cli_len);
	if (nfd == INVALID_SOCKET) {
		dprintf("ServerSocket:: Failed to accept connection\n");
		return NULL;
	}
	return new Socket(nfd);
}

/* binds the created socket to the port */
bool ServerSocket :: sock_connect() {
	if(!socket_setup())
		return false;
	int status = bind(fd, (struct sockaddr*)&addr, sizeof(addr));
	if(status == SOCKET_ERROR) {
		dprintf("ServerSocket:: Failed to bind socket\n");
		return false;
	}
	listen(fd, maxConns);
	return true;
}
