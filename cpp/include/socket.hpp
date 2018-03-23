/* 
 * file : socket.hpp
 * socket objects used for wrapping up raw-tcp sockets
 *
 * contributors:
 * 	Kartik Kalaghatagi [28 Oct 2017]
 * 	Madhusoodan Pataki [7 Nov 2017]
 */

// multi-include guard.
#ifndef SOCKET_INCLUDED
#define SOCKET_INCLUDED

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

#define SOCK_BUFFER_SIZE 1024
#define EOSS (-1)

#include <iostream>
#include "debug.hpp"
#include "stream.hpp"
using namespace std;

class Socket : public Stream {

protected:
	struct sockaddr_in addr;
	sock_t fd;
	bool alive;

	bool socket_setup();
private:
	string host;
	ushort port;
#ifdef __WIN32
	static WSADATA wsa;
#endif
	bool sock_connect();

public:
	void set_sock(string host, ushort port);
	Socket();
	Socket(sock_t fd);
	Socket(string host, ushort port);
	~Socket();

	bool writeData(string message);			/* To write strings to socket */
	string readData();						/* To read strings to socket */
	int readBytes(void *ptr, int size);		/* low level API to read data */
	int writeBytes(void *ptr, int size);	/* low level API to write data */
	ushort getPort();						/* To get port number */
	string getHost();						/* To get IP Address */
	void closeConn();						/* To close connection */
	bool isAlive();							/* To check connection liveness */
};

class ServerSocket : public Socket {

private:
	ushort maxConns;
	sockaddr_in c_addr;
	string getLocalHost();
	bool sock_connect();
public:
	ServerSocket(ushort port, ushort maxConns);
	Socket* acceptConn();
};

#endif