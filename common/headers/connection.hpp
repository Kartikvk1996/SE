#ifndef _STDC_H
#define _STDC_H
#include<bits/stdc++.h>
#endif // _STDC

#ifndef _CONFIGURE_H
#include"../../CRSMaster/headers/configure.hpp"
#endif // _CONFIGURE_H

#ifndef _FORMATTER_H
#include"formatter.hpp"
#endif // _FORMATTER_H

#ifndef _CONNECTION_H
#define _CONNECTION_H
#include<netinet/in.h>
#include<fcntl.h>
#include<sys/stat.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<unistd.h>
#include<string.h>
#include<arpa/inet.h>
#include<netdb.h>

#define CSOCKET int


using namespace std;


namespace __CONNECTION__
{

static const short int SOCKET_SUCC= 1;
static const short int WRITE_SUCC= 2;
static const short int SOCKET_FAIL= -1;
static const short int BIND_FAIL= -2;
static const short int ACCEPT_FAIL= -3;
static const short int READ_FAIL= -4;
static const short int WRITE_FAIL= -5;

    class SocketException :  public exception
    {
    private:
        int status=0;
    public:
        SocketException(int eid):status(eid) {}

    public:
        const char* what() const throw()
        {
            switch(status)
            {
                case SOCKET_FAIL : return "Failed to create Socket";
                case BIND_FAIL : return "Failed to bind Socket to IP";
                case ACCEPT_FAIL : return "Failed to accept new Connection";
                case READ_FAIL : return "Failed to read data from socket stream";
                case WRITE_FAIL : return "Failed to write data to socket stream";
                default : return "Undefined";
            }
        }
    };




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

    public:

        ServerConnection(string ipAddr,unsigned short int pt,unsigned short int mReq=10)
        {
            this->maxRequests=mReq;
            this->port=pt;
            this->ipAddress=ipAddr;
            __init();
        }

        ~ServerConnection()
        {
            closeConnection();
            serverAlive=false;
        }

    private:
        void __init();
        void serverConnect();

    public:
        CSOCKET acceptConnection();              /* Accept the new connection    */
        void writeData(CSOCKET ,string);         /* To write data to socket  */
        string readData(CSOCKET);                /* To read data to socket  */
        unsigned short int getPort();           /* To get port number  */
        string getIpAddress();                  /* To get IP Address  */
        void closeConnection();                 /* To close connection  */
        bool isAlive();                         /* To check connection is live or not   */
    };

    /*--------------------------------------------------END OF CLASS DECLARATION----------------------------------------------------*/







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


    public:
        /*  Constructor */

        ClientConnection(string ipAddr,unsigned short int pt)
        {
            this->port=pt;
            this->ipAddress=ipAddr;
            __init();
        }

        ~ClientConnection()
        {
            closeConnection();
            clientAlive=false;
        }


    private:
        void __init();
        void clientConnect();

    public:
        void writeData(string message);         /* To write data to socket  */
        string readData();                      /* To read data from socket */
        unsigned short int getPort();           /* To get port value        */
        string getIpAddress();                  /* To get IP Address        */
        void closeConnection();                 /* To Close Connection      */
        bool isAlive();                         /* To check connection is live or not   */
    };

    /*--------------------------------------------------END OF CLASS DECLARATION----------------------------------------------------*/

}

#endif // _CONNECTION_H

