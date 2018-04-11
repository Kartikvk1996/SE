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



#define CSOCKET int

using namespace std;


/*------------------------------------------------------SERVER CONNECTION CLASS--------------------------------------------------------*/

class ServerConnection
{
private:
    int serverFileDescriptor;
    struct sockaddr_in serverAddress,clientAddress;
    socklen_t clientRequestLength;


private:
    unsigned short int maxRequests;
    unsigned short int port;
    string ipAddress;
    char *msg=NULL;
    string message;
    bool serverAlive=false;
    LOG *log;

public:

    ServerConnection(string ipAddr,unsigned short int pt,unsigned short int mReq=10)
    {
        log=new LOG(CONFIG::SOCKET_LOGFILE,CONFIG::SOCKET_LOGDATEWISE,CONFIG::SOCKET_CNSLOGGING,CONFIG::SOCKET_CNSCOLOURING);
        msg=(char *)malloc(sizeof(char)*CONFIG::LBUFFERSIZE);
        assert(msg!=NULL);
        this->maxRequests=mReq;
        this->port=pt;
        this->ipAddress=ipAddr;
        if(!serverConnect())
        {
            log->write(LOG::ERROR,"[%s]\tFailed to start server with IP %s:%d",__func__,this->ipAddress.c_str(),this->port);
            serverAlive=false;
        }
        else
        {
            serverAlive=true;
        }
    }

    ~ServerConnection()
    {
        delete log;
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

    /* Opens the socket connections */
    this->serverFileDescriptor = socket(AF_INET,SOCK_STREAM,0);
    if(this->serverFileDescriptor < 0)
    {
        log->write(LOG::ERROR,"[%s]\tFailed to create socket",__func__);
        return false;
    }
    log->write(LOG::INFO,"[%s]\tNew socket created successfully",__func__);
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
        log->write(LOG::ERROR,"[%s]\tFailed to bind socket with IP %s:%d",__func__,this->ipAddress.c_str(),this->port);
        return false;
    }
    log->write(LOG::INFO,"[%s]\tSocket binded with IP %s:%d successfully",__func__,this->ipAddress.c_str(),this->port);
    listen(this->serverFileDescriptor,this->maxRequests);
    return true;
}

CSOCKET ServerConnection::acceptConnection()
{

    /* accept the request   */
    clientRequestLength=sizeof(clientAddress);

    CSOCKET newsockfd;

    newsockfd=accept(this->serverFileDescriptor, (struct sockaddr *) &clientAddress, &clientRequestLength);
    if(newsockfd<0)
    {
        log->write(LOG::WARNING,"[%s]\tFailed to accept new connection request",__func__);
        return -1;
    }
    return newsockfd;

}





/*  Method writeData() writes the data to the socket
    connection , returns true on success
    else false is returned on failure
*/

bool ServerConnection::writeData(CSOCKET fd,string message)
{
    unsigned short int status=write(fd,message.c_str(),message.length());
    if(status>0)
        return true;
    else
    {
        if(status==0)
        {
            log->write(LOG::WARNING,"[%s]\tFailed to write , connection closed by peer",__func__);
        }
        else
        {
            log->write(LOG::WARNING,"[%s]\tFailed to write to peer ERROR_CODE : [%s]",__func__,status);
        }
        return false;
    }
}



/*  Method readData() reads the data from the the socket
    connection returns string containing read data on success
    else NULL value is returned on failure
*/
string ServerConnection::readData(CSOCKET fd)
{
    message.clear();
    unsigned short int status=read(fd,msg,CONFIG::LBUFFERSIZE);
    if(status>0)
    {
        message=msg;
        memset(msg, 0, CONFIG::LBUFFERSIZE);
        return message;
    }
    else
    {
        if(status==0)
            log->write(LOG::WARNING,"[%s]\tFailed to read , connection closed by peer",__func__);
        else
            log->write(LOG::WARNING,"[%s]\tFailed to read, ERROR_CODE : [%s]",__func__,status);
        return "NULL";
    }
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
    free(msg);
    serverAlive=false;
    close(this->serverFileDescriptor);
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
    int clientFileDescriptor,connectfd;
    struct sockaddr_in clientAddress;

private:
    unsigned short int port;
    string ipAddress;
    char *msg=NULL;
    string message;
    bool clientAlive=false;
    LOG *log;


public:
    /*  Constructor */

    ClientConnection(string ipAddr,unsigned short int pt)
    {
        log=new LOG(CONFIG::SOCKET_LOGFILE,CONFIG::SOCKET_LOGDATEWISE,CONFIG::SOCKET_CNSLOGGING,CONFIG::SOCKET_CNSCOLOURING);
        msg=(char *)malloc(sizeof(char)*CONFIG::LBUFFERSIZE);
        this->port=pt;
        this->ipAddress=ipAddr;
        if(!clientConnect())
        {
            //log->write(LOG::ERROR,"[%s]\tFailed to start server with IP %s:%d",__func__,this->ipAddress.c_str(),this->port);
            clientAlive=false;
        }
        else
        {
            clientAlive=true;
        }

    }

    ~ClientConnection()
    {
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
    this->clientFileDescriptor = socket(AF_INET,SOCK_STREAM,0);
    if(this->clientFileDescriptor < 0)
    {
        log->write(LOG::ERROR,"[%s]\tFailed to create socket",__func__);
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
        log->write(LOG::ERROR,"[%s]\tFailed to connect to server IP: %s:%d",__func__,this->ipAddress.c_str(),this->port);
        return false;
    }

    return true;

}

/*  Method writeData() writes the data to the socket
    connection , returns true on success
    else false is returned on failure
*/

bool ClientConnection::writeData(string message)
{
    unsigned short int status=send(this->clientFileDescriptor,message.c_str(),message.length(),MSG_DONTWAIT);
    if(status>0)
        return true;
    else
    {
        if(status==0)
        {
            log->write(LOG::WARNING,"[%s]\tFailed to write , connection closed by peer",__func__);
        }
        else
        {
            log->write(LOG::WARNING,"[%s]\tFailed to write to peer ERROR_CODE : [%s]",__func__,status);
        }
        return false;
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
            log->write(LOG::WARNING,"[%s]\tFailed to read, connection closed by peer",__func__);
        else
            log->write(LOG::WARNING,"[%s]\tFailed to read, ERROR_CODE : [%s]",__func__,status);
        return "NULL";
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
    delete log;
    free(msg);
    close(this->clientFileDescriptor);
    shutdown(this->clientFileDescriptor, SHUT_WR);
    clientAlive=false;

}

/* Method isAlive() tells connected to server or not
*/
bool ClientConnection::isAlive()
{
    return this->clientAlive;
}

/*--------------------------------------------------END OF CLASS DECLARATION----------------------------------------------------*/

