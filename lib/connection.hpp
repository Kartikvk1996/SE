/*
---------------------------------------------------------CONNECTION HEADER FILE--------------------------------------------------------

   ---------------------------------
   | AUTHOR  :   KARTIK KALAGHATAGI|
   |-------------------------------|
   | DATE    :   28-10-2017        |
   ---------------------------------

    Connection.hpp defines two class :-

    i)  ServerConnection class which helps to start server on a given IP Address and port
        it supports two constructors

        a) ServerConnection(string IP_Address,unsigned short int PORT,int Max_requests_handle)

        b) ServerConnection() // This takes IP Address = 127.0.0.1 ,PORT = 8080 as default


        This class provides following methods :-

        i)      bool serverConnect()                            :   which is private and is called for every new object
                                                                    and opens socket connection for given IP Address
                                                                    on success it returns true else false.

        ii)     socket accept()                                 :   Is a blocking function call that returns socket


        ii)     bool writeData(socket fd,string message)        :   This method take socket file descriptor & data in the form of string
                                                                    writes it to the socket stream, on success it returns
                                                                    true else false on failure.

        iii)    string readData(socket fd)                      :   This methods reads the data present in the socket
                                                                    stream provided as argument on success it returns string
                                                                    else NULL value is returned.

        iV)     int getPort()                                   :   Returns the port on which socket is open.

        v)      string getIpAddress()                           :   Returns the IP Address.

        vi)     void closeConnection()                          :   closes the socket connection.
                                                                    (NOTE : Not necessary to call compulsory on destroy of object
                                                                    this function is called automatically.)

        vii)    bool isAlive()                      :   Tells connection is live or not ,returns true if live else false.

    ii) ClientConnection class which helps client program to communicate with server
        it supports two constructors

        a) ClientConnection(string IP_Address,unsigned short int PORT)

        b) ClientConnection() // This takes IP Address = 127.0.0.1 ,PORT = 8080 as default

        This class provides following methods :-

        i)      bool clientConnect()                :   which is private and is called for every new object
                                                        and opens socket connection for given IP Address
                                                        on success it returns true else false.

        ii)     bool writeData(string message)      :   This method take data in the form of string
                                                        writes it to the socket on success it returns
                                                        true else false on failure.

        iii)    string readData()                   :   This methods reads the data present in the socket
                                                        stream on success it returns string else NULL value
                                                        is returned.

        iV)     int getPort()                       :   Returns the port on which socket is open.

        v)      string getIpAddress()               :   Returns the IP Address.

        vi)     void closeConnection()              :   closes the socket connection.
                                                        (NOTE : Not necessary to call compulsory on destroy of object
                                                         this function is called automatically.)

        vii)    bool isAlive()                      :   Tells connection is live or not ,returns true if live else false.


---------------------------------------------------------------------------------------------------------------------------------------
*/




/*------------------------------------------------------PLATFORM DEPENDENT LIBRARIES IMPORT--------------------------------------------*/


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
#endif // __WIN32



#define BUFFERSIZE 65535
#define CSOCKET int

using namespace std;


/*------------------------------------------------------SERVER CONNECTION CLASS--------------------------------------------------------*/

class ServerConnection
{
private:
    #ifdef __unix__
        int serverFileDescriptor;
        struct sockaddr_in serverAddress,clientAddress;
        socklen_t clientRequestLength;
    #elif __WIN32
        WSADATA wsa;
        SOCKET serverFileDescriptor ;
        struct sockaddr_in server , client;
        int sockfdSize;
    #endif // __unix__

private:
    unsigned short int maxRequests;
    unsigned short int port;
    string ipAddress;
    char *msg=NULL;
    string message;
    bool serverAlive=false;

public:
    /*  constructor */
    ServerConnection()
    {
        msg=(char *)malloc(sizeof(char)*BUFFERSIZE);
        this->maxRequests=100;
        this->port=8080;
        this->ipAddress="127.0.0.1";
        if(!serverConnect())
        {
            #ifdef DEBUG
                cout<<"SERVER :Failed to start server";
            #endif // DEBUG
            serverAlive=true;
        }
        else
        {
            serverAlive=true;
        }
    }

    ServerConnection(string ipAddr,unsigned short int pt,unsigned short int mReq=10)
    {
        msg=(char *)malloc(sizeof(char)*BUFFERSIZE);
        this->maxRequests=mReq;
        this->port=pt;
        this->ipAddress=ipAddr;
        if(!serverConnect())
        {
            #ifdef DEBUG
                cout<<"SERVER :Failed to start server";
            #endif // DEBUG
            serverAlive=true;
        }
        else
        {
            serverAlive=true;
        }
    }

    ~ServerConnection()
    {
        free(msg);
        closeConnection();
        serverAlive=false;
    }

private:
    bool serverConnect();

public:
    CSOCKET acceptConnection();              /* Accept the new connection    */
    bool writeData(CSOCKET ,string);         /* To write data to socket  */
    string readData(CSOCKET);                /* To read data to socket  */
    unsigned short int getPort();           /* To get port number  */
    string getIpAddress();                  /* To get IP Address  */
    void closeConnection();                 /* To close connection  */
    bool isAlive();                         /* To check connection is live or not   */
};

/*--------------------------------------------------END OF CLASS DECLARATION----------------------------------------------------*/









/*-----------------------------------------SERVER CONNECTION CLASS METHODS IMPLEMENTATION--------------------------------------*/

/*  Method serverConnect opens connection for a given IP Address
    and port on success returns true , on failure returns false
*/

bool ServerConnection::serverConnect()
{
    #ifdef __unix__
    {
        /* Opens the socket connections */
        this->serverFileDescriptor = socket(AF_INET,SOCK_STREAM,0);
        if(this->serverFileDescriptor < 0)
        {
            #ifdef DEBUG
            {
                cout<<"SERVER :Failed to create socket\n";
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
                cout<<"SERVER :Failed to bind socket\n";
            }
            #endif // DEBUG
        }

        listen(this->serverFileDescriptor,this->maxRequests);
        return true;
    }
    #elif __WIN32
    {
        if (WSAStartup(MAKEWORD(2,2),&wsa) != 0)
        {
            #ifdef DEBUG
                cout<<"SERVER :Failed. Error Code : "<<WSAGetLastError()<<"\n";
            return false;
            #endif // DEBUG
        }


        if((this->serverFileDescriptor = socket(AF_INET , SOCK_STREAM , 0 )) == INVALID_SOCKET)
        {
            #ifdef DEBUG
                cout<<"SERVER :Failed to create socket : "<< WSAGetLastError()<<"\n";
            return false;
            #endif // DEBUG

        }

        server.sin_family = AF_INET;
        server.sin_addr.s_addr = inet_addr(this->ipAddress.c_str());
        server.sin_port = htons( this->port );

        if( bind(this->serverFileDescriptor ,(struct sockaddr *)&server , sizeof(server)) == SOCKET_ERROR)
        {
            #ifdef DEBUG
                cout<<"SERVER :Bind failed with error code : "<< WSAGetLastError()<<"\n";
            return false;
            #endif // DEBUG

        }

        listen(this->serverFileDescriptor , this->maxRequests);
        return true;
    }
    #endif

}

CSOCKET ServerConnection::acceptConnection()
{
    #ifdef __unix__
    {
        /* accept the request   */
        clientRequestLength=sizeof(clientAddress);

        CSOCKET newsockfd;

        newsockfd=accept(this->serverFileDescriptor, (struct sockaddr *) &clientAddress, &clientRequestLength);
        if(newsockfd<0)
        {
            #ifdef DEBUG
                cout<<"SERVER :Failed to accept connection\n";
            #endif // DEBUG
            return -1;
        }
        return newsockfd;
    }
    #elif __WIN32
    {
        this->sockfdSize = sizeof(struct sockaddr_in);
        CSOCKET newsockfd;

        if((newsockfd = accept(this->serverFileDescriptor , (struct sockaddr *)&this->client, &this->sockfdSize)) != INVALID_SOCKET )
        {
            return newsockfd;
        }
        else
        {
            #ifdef DEBUG
                cout<<"SERVER :Failed to accept connection\n";
            #endif // DEBUG
            return -1;
        }
    }
    #endif // __unix__
}





/*  Method writeData() writes the data to the socket
    connection , returns true on success
    else false is returned on failure
*/

bool ServerConnection::writeData(CSOCKET fd,string message)
{
    #ifdef __unix__
    {
        if(write(fd,message.c_str(),message.length())>0)
            return true;
        else
        {
            #ifdef DEBUG
                cout<<"SERVER :Failed to write to socket\n";
            #endif // DEBUG
            return false;
        }
    }
    #elif __WIN32
    {
        if((send(fd , message.c_str() , message.length() , 0))>0)
            return true;
        else
        {
            #ifdef DEBUG
                cout<<"SERVER :Failed to write to socket\n";
            #endif // DEBUG
            return false;
        }

    }
    #endif // __unix__

}



/*  Method readData() reads the data from the the socket
    connection returns string containing read data on success
    else NULL value is returned on failure
*/
string ServerConnection::readData(CSOCKET fd)
{

    #ifdef __unix__
    {

        if(read(fd,msg,BUFFERSIZE)>0)
        {
            message=msg;
            return message;
        }
        else
        {
            #ifdef DEBUG
                cout<<"SERVER :Failed to read from socket\n";
            #endif // DEBUG
            return NULL;
        }
    }
    #elif __WIN32
    {
        if((recv(fd,msg,BUFFERSIZE,0))<0)
        {
            #ifdef DEBUG
                cout<<"SERVER :Failed to read data to socket\n";
            #endif // DEBUG
            return NULL;
        }
        else
        {
            message=msg;
            return message;
        }
    }
    #endif // __unix__
}


/*  Method getPort() returns the port value on which socket
    connection is open
*/
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
    serverAlive=false;
    #ifdef __unix__
    {
        close(this->serverFileDescriptor);
    }
    #elif __WIN32
    {
        closesocket(this->serverFileDescriptor);
        WSACleanup();
    }
    #endif // __unix__
}

/* Method isAlive() tells connected to server or not
*/
bool ServerConnection::isAlive()
{
    return this->serverAlive;
}


/*----------------------------------------END OF IMPLEMENTATION OF SERVER CONNECTION CLASS METHODS------------------------------------*/






/*-------------------------------------------------CLIENT CONNECTION CLASS-------------------------------------------------------------*/



class ClientConnection
{
private:
    #ifdef __unix__
        int clientFileDescriptor,connectfd;
        struct sockaddr_in clientAddress;
    #elif __WIN32
        WSADATA wsda;
        struct sockaddr_in server;
        int serverfd;
    #endif // __unix__

private:
    unsigned short int port;
    string ipAddress;
    char *msg=NULL;
    string message;
    bool clientAlive=false;


public:
    /*  Constructor */
    ClientConnection()
    {
        msg=(char *)malloc(sizeof(char)*BUFFERSIZE);
        this->port=8080;
        this->ipAddress="127.0.0.1";
        if(!clientConnect())
        {
            #ifdef DEBUG
                cout<<"CLIENT :Failed to connect to Server";
            #endif // DEBUG
        }
    }


    ClientConnection(string ipAddr,unsigned short int pt)
    {
        msg=(char *)malloc(sizeof(char)*BUFFERSIZE);
        this->port=pt;
        this->ipAddress=ipAddr;
        if(!clientConnect())
        {
            #ifdef DEBUG
                cout<<"CLIENT :Failed to connect to Server";
            #endif // DEBUG
            clientAlive=false;
        }
        else
        {
            clientAlive=true;
        }

    }

    ~ClientConnection()
    {
        free(msg);
        closeConnection();
        clientAlive=false;
    }


private:
    bool clientConnect();

public:
    bool writeData(string message);         /* To write data to socket  */
    string readData();                      /* To read data from socket */
    unsigned short int getPort();           /* To get port value        */
    string getIpAddress();                  /* To get IP Address        */
    void closeConnection();                 /* To Close Connection      */
    bool isAlive();                         /* To check connection is live or not   */
};

/*--------------------------------------------------END OF CLASS DECLARATION----------------------------------------------------*/









/*-----------------------------------------CLIENT CONNECTION CLASS METHODS IMPLEMENTATION--------------------------------------*/

/*  Method clientConnect opens connection for a given IP Address
    and port on success returns true , on failure returns false
*/

bool ClientConnection::clientConnect()
{
    #ifdef __unix__
        this->clientFileDescriptor = socket(AF_INET,SOCK_STREAM,0);
        if(this->clientFileDescriptor < 0)
        {
            #ifdef DEBUG
            {
                cout<<"CLIENT :Failed to create socket\n";
            }
            #endif // DEBUG

            return false;
        }


        /* setup address family of internet */
        clientAddress.sin_family=AF_INET;

        /* Listen to any internal address */
        clientAddress.sin_addr.s_addr = inet_addr(this->ipAddress.c_str());
        clientAddress.sin_port=htons(this->port);

        connectfd=connect(this->clientFileDescriptor,(struct sockaddr *) &clientAddress, sizeof(clientAddress));
        if(connectfd<0)
        {
            #ifdef DEBUG
            {
                cout<<"CLIENT :Failed to connect to server\n";
            }
            #endif // DEBUG
            return false;
        }

        return true;
    #elif __WIN32
        WSAStartup(0x0101,&wsda);
        if((serverfd=socket(AF_INET,SOCK_STREAM,0))==-1)
        {
            #ifdef DEBUG
                cout<<"CLIENT :Failed to open to socket\n";
            #endif // DEBUG
            return false;

        }


        /* setup address family of internet */
        server.sin_family=AF_INET;

        /* Listen to any internal address */
        server.sin_addr.s_addr = inet_addr(this->ipAddress.c_str());
        server.sin_port=htons(this->port);


        if(connect(serverfd,(struct sockaddr*)&server,sizeof(struct sockaddr))==-1)
        {
            #ifdef DEBUG
                cout<<"CLIENT :Failed to connect to server\n";
            #endif // DEBUG
            return false;
        }
        return true;
    #endif // __unix__

}

/*  Method writeData() writes the data to the socket
    connection , returns true on success
    else false is returned on failure
*/

bool ClientConnection::writeData(string message)
{
    #ifdef __unix__
        if(write(this->connectfd,message.c_str(),message.length())>0)
            return true;
        else
        {
            #ifdef DEBUG
                cout<<"CLIENT :Failed to write to socket\n";
            #endif // DEBUG
            return false;
        }
    #elif __WIN32
        if((send(serverfd,message.c_str(),message.length(),0))>0)
            return true;
        else
        {
            #ifdef DEBUG
                cout<<"CLIENT :Failed to write to socket\n";
            #endif // DEBUG
            return false;
        }

    #endif // __unix__

}


/*  Method readData() reads the data from the the socket
    connection returns string containing read data on success
    else NULL value is returned on failure
*/
string ClientConnection::readData()
{
    #ifdef __unix__
        if((read(this->clientFileDescriptor,msg,BUFFERSIZE))>0)
        {
            message=msg;
            return message;
        }
        else
        {
            #ifdef DEBUG
                cout<<"CLIENT :Failed to read from socket\n";
            #endif // DEBUG
            return NULL;
        }
    #elif __WIN32
        if((recv(serverfd,msg,BUFFERSIZE,0))>0)
        {
            message=msg;
            return message;
        }
        else
        {
            #ifdef DEBUG
                cout<<"CLIENT :Failed to read from socket\n";
            #endif // DEBUG
            return NULL;
        }
    #endif // __unix__

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
    #ifdef __unix__
        close(this->clientFileDescriptor);
    #elif __WIN32
        closesocket(this->serverfd);
        WSACleanup();
    #endif // __unix__
    clientAlive=false;

}

/* Method isAlive() tells connected to server or not
*/
bool ClientConnection::isAlive()
{
    return this->clientAlive;
}

/*--------------------------------------------------END OF CLASS DECLARATION----------------------------------------------------*/

