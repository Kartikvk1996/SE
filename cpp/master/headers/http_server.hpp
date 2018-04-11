/* Header dependency
    ctpl_stl.h
*/

#define HTTP_MAX_THREADS 20

class HttpServer
{

private:
	ServerConnection *sc;
	string ipAddress;
	unsigned short int port;
	ctpl::thread_pool *tpool;
	CMS *object;
	bool stopServer=false;
	map<string,int> mapper;

public:
	HttpServer(string ipAddr,unsigned int port,unsigned int threads,CMS *obj)
	{
		this->ipAddress=ipAddr;
		this->port=port;
		sc=new ServerConnection(this->ipAddress,this->port);
		if(threads>HTTP_MAX_THREADS)
			threads=HTTP_MAX_THREADS;
		tpool=new ctpl::thread_pool(threads);
		this->object=obj;
	}

    ~HttpServer()
    {
        delete sc;
    }

	void runner();
	void handleRequest(CSOCKET fd);
};

void HttpServer::runner()
{
    mapper["master"]=1;
    mapper["queue"]=2;
    mapper["logs"]=3;
    mapper["searchQueue"]=4;
    mapper["processInfo"]=5;
    mapper["shutdown"]=6;

	while(!stopServer)
	{
		CSOCKET fd=sc->acceptConnection();
		if(fd>0)
        {
            tpool->push([&,this](int id){
			handleRequest(fd);
            });
        }
    }
	return;
}

void HttpServer::handleRequest(CSOCKET fd)
{
    char buffer[CONFIG::MBUFFERSIZE];
    HttpParser *hp=new HttpParser(sc->readData(fd));
    bool is_json=true;
    // Experimental
    // Next to use std::map with function ptr to call functions
    string sendData;
    int choice=mapper[hp->filePath];
    switch(choice)
    {
        case 1  :sendData=object->statusInfo();
                break;
        case 2  :sendData=object->que->statusInfo(hp->issetGET("showlinks"));
                break;
        case 3  :   {
                        stringstream ss;ss<<"logs/"<<hp->_GET("name")<<".log";
                        ifstream in(ss.str());
                        if(in.is_open()==false)
                            return;
                        string send((std::istreambuf_iterator<char>(in) ),(std::istreambuf_iterator<char>()));
                        sendData=send;
                        in.close();
                    }
                break;
        case 4  :sendData=object->que->searchQueue(hp->_GET("url"),hp->issetGET("similar"));
                break;
        case 5  :sendData=object->systemInfo();
                break;
        case 6  :object->shutdown(); stopServer=true;
                break;
        default : is_json=false;
                ifstream in("www/index.html");
                if(in.is_open()==false)
                    return;
                string send((std::istreambuf_iterator<char>(in) ),(std::istreambuf_iterator<char>()));
                sendData=send;
                in.close();
    }

    if(is_json==true)
    {
        sprintf(buffer,"HTTP/1.1 200 OK\n"
                        "Server: HttpServer\n"
                        "Content-Type: text/json\n"
                        "Content-Length: %ld\n"
                        "Accept-Ranges: bytes\n"
                        "Connection: close\n"
                        "\n"
                        "%s",sendData.length(),sendData.c_str());
    }
    else
    {
        sprintf(buffer,"HTTP/1.1 200 OK\n"
                            "Server: HttpServer\n"
                            "Content-Type: text/html\n"
                            "Content-Length: %ld\n"
                            "Accept-Ranges: bytes\n"
                            "Connection: close\n"
                            "\n"
                            "%s",sendData.length(),sendData.c_str());
    }


    sc->writeData(fd,buffer);
    delete hp;
    close(fd);

    if(stopServer==true)
    {
        ClientConnection close(this->ipAddress,this->port);
    }

    return;
}



