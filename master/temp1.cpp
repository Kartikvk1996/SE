
#include<iostream>

#include"connection.hpp"

using namespace std;


int main()
{
    ClientConnection *sc=new ClientConnection("127.0.0.1",8123);
    sc->writeData("Hello bahdko");
    cout<<sc->readData();
    sc->closeConnection();
    return 0;
}
