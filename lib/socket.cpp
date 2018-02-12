#include "socket.hpp"

void Socket::set_sock(string host, ushort port) {
    this->host = host;
    this->port = port;
}

Socket::Socket() { /* default do nothing */}

Socket::Socket(sock_t fd) {
    this->fd = fd;
}

Socket::Socket(string host, ushort port) {
    set_sock(host, port);
    if (!(alive = sock_connect())) {
        dprintf("Socket:: failed to connect\n");
    }
}

Socket::~Socket() {
    closeConn();
}

bool Socket ::socket_setup() {

#ifdef __WIN32
    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0)
    {
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
bool Socket ::sock_connect() {
    if (!socket_setup())
        return false;
    if (connect(fd, (struct sockaddr *)&addr, sizeof(addr)) == -1) {
        dprintf("Socket:: failed to connect to server\n");
        return false;
    }
    return true;
}

int Socket ::writeBytes(void *buffer, int size) {
    return write(fd, buffer, size);
}

int Socket ::readBytes(void *buffer, int size) {
    return read(fd, buffer, size);
}

/*  writes 'data' on socket returns true as success, false otherwise. */
bool Socket ::writeData(string data) {

    char nullb = -1;

    if ((write(fd, data.c_str(), data.length()) >= 0) &&
        (write(fd, &nullb, 1) >= 0)) {
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
string Socket ::readData()
{
    string str = "";
    int status;
    char buffer[SOCK_BUFFER_SIZE];
    try {
        while((status = read(fd, buffer, SOCK_BUFFER_SIZE-1)) >= 0) {

            int end = (buffer[status - 1] == EOSS) ? status - 1 : status;
            buffer[end] = '\0';
            str += buffer;
            if(status != end) {
                break;
            }
        }
        if(buffer[status - 1] == -1) {
            dprintf("Socket:: Failed to read from socket\n");
        }
    }
    catch (...) {}
    return str;
}

/* returns the port value on which socket connection is open */
ushort Socket ::getPort() {
    return port;
}

/* returns the Host Address */
string Socket ::getHost() {
    return host;
}

/* closes the socket connection */
void Socket ::closeConn() {
    alive = false;
    close(fd);
}

/* TODO: [Is it what we need] tells whether we are connected? */
bool Socket ::isAlive() {
    return alive;
}



/* server socket implementation */

ServerSocket::ServerSocket(string host, ushort port, ushort maxConns) : Socket() {
    set_sock(host, port);
    this->maxConns = maxConns;
    if (!(alive = ServerSocket::sock_connect())) {
        dprintf("ServerSocket:: Failed to start server\n");
    }
}

/* returns the socket connected to a client */
Socket *ServerSocket ::acceptConn() {
    socklen_t cli_len = sizeof(c_addr);
    sock_t nfd = accept(fd, (struct sockaddr *)&c_addr, &cli_len);
    if (nfd == INVALID_SOCKET) {
        dprintf("ServerSocket:: Failed to accept connection\n");
        return NULL;
    }
    return new Socket(nfd);
}

/* binds the created socket to the port */
bool ServerSocket ::sock_connect() {
    if (!socket_setup())
        return false;
    int status = bind(fd, (struct sockaddr *)&addr, sizeof(addr));
    if (status == SOCKET_ERROR) {
        dprintf("ServerSocket:: Failed to bind socket\n");
        return false;
    }
    listen(fd, maxConns);
    return true;
}
