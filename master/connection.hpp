#include<netinet/in.h>
#include<fcntl.h>
#include<sys/stat.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<unistd.h>
#include<string.h>
#include<arpa/inet.h>
#include<netdb.h>


#define DEBUG 1;

using namespace std;

class ServerConnection
{
private:
    unsigned short int maxRequests;
    int serverFileDescriptor;
    unsigned short int port;
    string ipAddress;
    string message;
    struct sockaddr_in serverAddress,clientAddress;
    int newsockfd;
    socklen_t clientRequestLength;

public:
    ServerConnection()
    {
        this->maxRequests=100;
        this->port=8080;
        this->ipAddress="127.0.0.1";
        this->message="";
        if(!serverConnect())
            cout<<"Failed to start server";
    }

    ServerConnection(string ipAddr,unsigned short int pt,unsigned short int mReq=10)
    {
        this->maxRequests=mReq;
        this->port=pt;
        this->ipAddress=ipAddr;
        this->message="";
        if(!serverConnect())
            cout<<"Failed to start server";
    }


private:
    bool serverConnect();

public:
    bool writeData(string message);
    string readData();
    unsigned short int getPort();
    string getIpAddress();
    void closeConnection();
};



bool ServerConnection::serverConnect()
{
    this->serverFileDescriptor = socket(AF_INET,SOCK_STREAM,0);
    if(this->serverFileDescriptor < 0)
    {
        #ifdef DEBUG
        {
            cout<<"Failed to create socket\n";
        }
        #endif // DEBUG

        return false;
    }

    /* set all the content to zero  */
    bzero((char *)&serverAddress,sizeof(serverAddress));

    /* setup address family of internet */
    serverAddress.sin_family=AF_INET;

    /* Listen to any internal address */
    serverAddress.sin_addr.s_addr = inet_addr(this->ipAddress.c_str());
    serverAddress.sin_port=htons(this->port);

    int bind_stat=bind(this->serverFileDescriptor,(struct sockaddr *) &serverAddress, sizeof(serverAddress));
    if(bind_stat<0)
    {
        #ifdef DEBUG
        {
            cout<<"Failed to bind socket\n";
        }
        #endif // DEBUG
    }

    listen(this->serverFileDescriptor,this->maxRequests);
    return true;
}


bool ServerConnection::writeData(string message)
{
    if(write(this->newsockfd,message.c_str(),message.length())>0)
        return true;
    else
    {
        #ifdef DEBUG
            cout<<"Failed to write to socket\n";
        #endif // DEBUG
        return false;
    }
}

string ServerConnection::readData()
{
    /* accept the request   */

    clientRequestLength=sizeof(clientAddress);
    newsockfd=accept(this->serverFileDescriptor, (struct sockaddr *) &clientAddress, &clientRequestLength);
    if(newsockfd<0)
    {
        #ifdef DEBUG
            cout<<"Failed to accept connection\n";
        #endif // DEBUG
        return "-1";
    }

    if(read(this->newsockfd,&message[0],65535)>0)
    {
        return message;
    }
    else
    {
        #ifdef DEBUG
            cout<<"Failed to read from socket\n";
        #endif // DEBUG
        return "-1";
    }
}


unsigned short int ServerConnection::getPort()
{
    return this->port;
}


string ServerConnection::getIpAddress()
{
    return ipAddress;
}


void ServerConnection::closeConnection()
{
    close(this->serverFileDescriptor);
}








/*-----------------------------------------------------------CLIENT CLASS-------------------------------------------------------------*/



class ClientConnection
{
private:
    int clientFileDescriptor;
    unsigned short int port;
    string ipAddress;
    string message;
    struct sockaddr_in clientAddress;

public:
    ClientConnection()
    {
        this->port=8080;
        this->ipAddress="127.0.0.1";
        this->message="";
        if(!clientConnect())
            cout<<"Failed to connect to server";
    }


    ClientConnection(string ipAddr,unsigned short int pt)
    {
        this->port=pt;
        this->ipAddress=ipAddr;
        this->message="";
        if(!clientConnect())
            cout<<"Failed to connect to server";
    }


private:
    bool clientConnect();

public:
    bool writeData(string message);
    string readData();
    unsigned short int getPort();
    string getIpAddress();
    void closeConnection();
};



bool ClientConnection::clientConnect()
{
    this->clientFileDescriptor = socket(AF_INET,SOCK_STREAM,0);
    if(this->clientFileDescriptor < 0)
    {
        #ifdef DEBUG
        {
            cout<<"Failed to create socket\n";
        }
        #endif // DEBUG

        return false;
    }


    /* setup address family of internet */
    clientAddress.sin_family=AF_INET;

    /* Listen to any internal address */
    clientAddress.sin_addr.s_addr = inet_addr(this->ipAddress.c_str());
    clientAddress.sin_port=htons(this->port);

    int connectStat=connect(this->clientFileDescriptor,(struct sockaddr *) &clientAddress, sizeof(clientAddress));
    if(connectStat<0)
    {
        #ifdef DEBUG
        {
            cout<<"Failed to connect to server\n";
        }
        #endif // DEBUG
    }

    return true;
}


bool ClientConnection::writeData(string message)
{
    if(write(this->clientFileDescriptor,message.c_str(),message.length())>0)
        return true;
    else
    {
        #ifdef DEBUG
            cout<<"Failed to write to socket\n";
        #endif // DEBUG
        return false;
    }
}

string ClientConnection::readData()
{
    if(read(this->clientFileDescriptor,&message[0],65535)>0)
    {
        return message;
    }
    else
    {
        #ifdef DEBUG
            cout<<"Failed to read from socket\n";
        #endif // DEBUG
        return "-1";
    }
}


unsigned short int ClientConnection::getPort()
{
    return this->port;
}


string ClientConnection::getIpAddress()
{
    return ipAddress;
}


void ClientConnection::closeConnection()
{
    close(this->clientFileDescriptor);
}
