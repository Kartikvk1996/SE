#include<bits/stdc++.h>
#include"configure.hpp"
#include"connection.hpp"


using namespace std;
using ServerConnection=__CONNECTION__::ServerConnection;
using SocketException=__CONNECTION__::SocketException;
int main()
{
    ServerConnection *sc=new ServerConnection("127.999.0.1",8124);
    try
    {
        int a=sc->acceptConnection();
    }
    catch(SocketException &e)
    {
        cout<<e.what();
    }
}
