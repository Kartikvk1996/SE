
#include<iostream>

#include"connection.hpp"

using namespace std;


int main()
{
    ClientConnection *sc=new ClientConnection("127.0.0.1",8080);
    sc->writeData("GET /config.txt HTTP/1.1\nHost: 127.0.0.1:8080\nConnection: keep-alive\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36\nAccept: image/webp,image/apng,image/*,*/*;q=0.8");//\nReferer: http://127.0.0.1:8080/http.h
    cout<<sc->readData();
    sc->closeConnection();
    return 0;
}
