#include<iostream>

#include"connection.hpp"
using namespace std;


int main()
{
    ServerConnection *sc=new ServerConnection("127.0.0.1",8123,100);
//    cout<<sc->readData();
//    cout<<sc->getIpAddress();
//    cout<<sc->getPort();
  	cout<<sc->readData();
    sc->writeData("Hello bassya");
    sc->closeConnection();
    return 0;
}
