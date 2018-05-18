#include"../headers/connection.hpp"

using ServerConnection=__CONNECTION__::ServerConnection;
using ClientConnection=__CONNECTION__::ClientConnection;

/*-----------------------------------------SERVER CONNECTION CLASS METHODS IMPLEMENTATION--------------------------------------*/

void ServerConnection::__init()
{
    //log=new LOG(CONFIG::SOCKET_LOGFILE,CONFIG::SOCKET_LOGDATEWISE,CONFIG::SOCKET_CNSLOGGING,CONFIG::SOCKET_CNSCOLOURING);

    msg=(char *)malloc(sizeof(char)*CONFIG::LBUFFERSIZE);
    assert(msg!=NULL);
    serverConnect();
    return;
}


/*  Method serverConnect opens connection for a given IP Address
    and port on success returns true , on failure returns false
*/

void ServerConnection::serverConnect()
{
    int reuse=1;
    /* Opens the socket connections */
    this->serverFileDescriptor = socket(AF_INET,SOCK_STREAM,0);
    if(this->serverFileDescriptor < 0)
    {
        //log->write(LOG::ERROR,"[%s]\tFailed to create socket",__func__);
        throw  SocketException(SOCKET_FAIL);
    }
    //log->write(LOG::INFO,"[%s]\tNew socket created successfully",__func__);
    /* set all the content to zero  */
    bzero((char *)&serverAddress,sizeof(serverAddress));

    /* setup address family of internet */
    serverAddress.sin_family=AF_INET;

    /* Listen to any internal address */
    serverAddress.sin_addr.s_addr = inet_addr(this->ipAddress.c_str());
    serverAddress.sin_port=htons(this->port);

    setsockopt(serverFileDescriptor,SOL_SOCKET,SO_REUSEADDR,&reuse,sizeof(int));

    int bind_stat=bind(this->serverFileDescriptor,(struct sockaddr *) &serverAddress, sizeof(serverAddress));
    if(bind_stat<0)
    {
        //log->write(LOG::ERROR,"[%s]\tFailed to bind socket with IP %s:%d",__func__,this->ipAddress.c_str(),this->port);
        throw  SocketException(BIND_FAIL);
    }
    //log->write(LOG::INFO,"[%s]\tSocket binded with IP %s:%d successfully",__func__,this->ipAddress.c_str(),this->port);
    listen(this->serverFileDescriptor,this->maxRequests);
    return;
}

CSOCKET ServerConnection::acceptConnection()
{

    /* accept the request   */
    clientRequestLength=sizeof(clientAddress);

    CSOCKET newsockfd;
    newsockfd=accept(this->serverFileDescriptor, (struct sockaddr *) &clientAddress, &clientRequestLength);
    if(newsockfd<0)
    {
        //log->write(LOG::WARNING,"[%s]\tFailed to accept new connection request",__func__);
        throw  SocketException(ACCEPT_FAIL);
    }
    int flags = fcntl(newsockfd, F_GETFL, 0);
    fcntl(newsockfd, F_SETFL, flags | O_NONBLOCK);
    return newsockfd;
}

void ServerConnection::writeData(CSOCKET fd,string message)
{
    unsigned short int status=send(fd,message.c_str(),message.length(),MSG_NOSIGNAL);
    if(status>0)
        return;
    else
    {
        if(status==0)
        {
            throw  std::runtime_error(Formatter()<<"["<<__func__<<"]\tFailed to write to socket. Connection closed by peer, Errorno :"<<errno);
        }
        else
        {
            throw  SocketException(WRITE_FAIL);
        }
    }
}

string ServerConnection::readData(CSOCKET fd)
{
    message.clear();
    message="NULL";
    unsigned short int status=recv(fd,msg,CONFIG::LBUFFERSIZE,0);
    if(status>0)
    {
        message=msg;
        memset(msg, 0, CONFIG::LBUFFERSIZE);
        return message;
    }
    else
    {
        if(status==0)
            throw  std::runtime_error(Formatter()<<"["<<__func__<<"]\tFailed to read from socket. Connection closed by peer, Errorno :"<<errno);
        else
            throw  SocketException(READ_FAIL);
    }
}


unsigned short int ServerConnection::getPort()
{
    return this->port;
}


/*  Method getIpAddress() returns the IP Address
*/
string ServerConnection::getIpAddress()
{
    return ipAddress;
}


/*  Method closeConnection() closes the socket connection
*/
void ServerConnection::closeConnection()
{
    if(msg!=NULL);
        free(msg);
    serverAlive=false;
    shutdown(this->serverFileDescriptor, SHUT_WR);
}

/* Method isAlive() tells connected to server or not
*/
bool ServerConnection::isAlive()
{
    return this->serverAlive;
}


/*----------------------------------------END OF IMPLEMENTATION OF SERVER CONNECTION CLASS METHODS------------------------------------*/











/*-----------------------------------------CLIENT CONNECTION CLASS METHODS IMPLEMENTATION--------------------------------------*/

void ClientConnection::__init()
{
    //log=new LOG(CONFIG::SOCKET_LOGFILE,CONFIG::SOCKET_LOGDATEWISE,CONFIG::SOCKET_CNSLOGGING,CONFIG::SOCKET_CNSCOLOURING);
    msg=(char *)malloc(sizeof(char)*CONFIG::LBUFFERSIZE);
    assert(msg!=NULL);
    clientConnect();
}

void ClientConnection::clientConnect()
{
    this->clientFileDescriptor = socket(AF_INET,SOCK_STREAM,0);
    if(this->clientFileDescriptor < 0)
    {
        throw  SocketException(SOCKET_FAIL);
    }
    /* setup address family of internet */
    clientAddress.sin_family=AF_INET;

    /* Listen to any internal address */
    clientAddress.sin_addr.s_addr = inet_addr(this->ipAddress.c_str());
    clientAddress.sin_port=htons(this->port);

    connectfd=connect(this->clientFileDescriptor,(struct sockaddr *) &clientAddress, sizeof(clientAddress));
    if(connectfd<0)
    {
        throw  std::runtime_error(Formatter()<<"["<<__func__<<"]\tFailed to connect to "<<this->ipAddress<<":"<<this->port);
    }
    return;
}

/*  Method writeData() writes the data to the socket
    connection , returns true on success
    else false is returned on failure
*/

void ClientConnection::writeData(string message)
{
    unsigned short int status=send(this->clientFileDescriptor,message.c_str(),message.length(),0);
    if(status>0)
        return;
    else
    {
        if(status==0)
            throw std::runtime_error(Formatter()<<"["<<__func__<<"]\tFailed to write to socket. Connection closed by peer, Errorno :"<<errno);
        else
            throw SocketException(WRITE_FAIL);
    }
}


/*  Method readData() reads the data from the the socket
    connection returns string containing read data on success
    else NULL value is returned on failure
*/
string ClientConnection::readData()
{
    message.clear();
    unsigned short int status=read(this->clientFileDescriptor,msg,CONFIG::LBUFFERSIZE);
    if(status>0)
    {
        message=msg;
        memset(msg,0,CONFIG::LBUFFERSIZE);
        return message;
    }
    else
    {
        if(status==0)
            throw  std::runtime_error(Formatter()<<"["<<__func__<<"]\tFailed to read from socket. Connection closed by peer, Errorno :"<<errno);
        else
            throw  SocketException(READ_FAIL);
    }
}

/*  Method getPort() returns the port value on which socket
    connection is open
*/
unsigned short int ClientConnection::getPort()
{
    return this->port;
}

/*  Method getIpAddress() returns the IP Address
*/
string ClientConnection::getIpAddress()
{
    return ipAddress;
}

/*  Method closeConnection() closes the socket connection
*/
void ClientConnection::closeConnection()
{
    if(msg!=NULL)
        free(msg);
    shutdown(this->clientFileDescriptor, SHUT_WR);
    clientAlive=false;

}

/* Method isAlive() tells connected to server or not
*/
bool ClientConnection::isAlive()
{
    return this->clientAlive;
}





