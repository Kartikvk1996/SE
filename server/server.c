#include<stdio.h>
#include<string.h>
#include<unistd.h>
#include<fcntl.h>
#include<sys/stat.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<stdlib.h>
#include<netinet/in.h>
#include<pthread.h>
#include<signal.h>

#include"ThreadPool/thpool.h"

// define port which is 8080 for 'http request'
#define PORT 8080
#define TOT_POOL_THREADS 10

threadpool global;

char types[10][50]={"Content-Type: text/html\r\n",
                     "Content-Type: application/javascript\r\n",
                     "Content-Type: application/json\r\n",
                     "Content-Type: text/css\r\n",
                     "Content-Type: image/png\r\n",
                     "Content-Type: image/jpeg\r\n",
                     "Content-Type: image/gif\r\n",
                     "Content-Type: application/pdf\r\n",
                     "Content-Type: text/plain\r\n"};




void check_send_status(int status)
{
    if(status<0)
    {
        printf("Error occured in sending data %s at line %d",__FUNCTION__,__LINE__);
    }
}

void send_http(int fd,int type)
{
    char reply[2][100] ={"HTTP/1.1 200 OK\r\n"
                     "Server :C++ _ Developed by kartik_v_k\r\n",
                        "HTTP/1.1 404 Not Found\r\n"
                     "Server :C++ _ Developed by kartik_v_k\r\n"};
    switch(type)
    {
        case 0:check_send_status(send(fd,reply[1],strlen(reply[1]),0));
                break;
        case 1:check_send_status(send(fd,reply[0],strlen(reply[0]),0));
                break;
    }
    return ;
}

int identify_file_type(char *path,int fd)
{
    if(strstr(path,".html")!=NULL)
    {
        send(fd,types[0],strlen(types[0]),0);
    }
    else if(strstr(path,".js")!=NULL)
    {
        send(fd,types[1],strlen(types[0]),0);
    }
    else if(strstr(path,".json")!=NULL)
    {
        send(fd,types[2],strlen(types[2]),0);
    }
    else if(strstr(path,".css")!=NULL)
    {
        send(fd,types[3],strlen(types[3]),0);
    }
    else if(strstr(path,".png")!=NULL)
    {
        send(fd,types[4],strlen(types[4]),0);
    }
    else if(strstr(path,".jpeg")!=NULL)
    {
        send(fd,types[5],strlen(types[5]),0);
    }
    else if(strstr(path,".gif")!=NULL)
    {
        send(fd,types[6],strlen(types[6]),0);
    }
    else if(strstr(path,".pdf")!=NULL)
    {
        send(fd,types[7],strlen(types[7]),0);
    }
    else if(strstr(path,".cgi")!=NULL)
    {
        send(fd,types[0],strlen(types[0]),0);
        return 1;
    }
    else if(strstr(path,".c")!=NULL)
    {
        send(fd,types[2],strlen(types[2]),0);
    }
    else if(strstr(path,".cpp")!=NULL)
    {
        send(fd,types[2],strlen(types[2]),0);
    }
    else
    {
        send(fd,types[8],strlen(types[8]),0);
    }
    return 0;

}



void handle_request(void *fd_ptr)
{
    int fd=*((int *)fd_ptr);
    char buffer[1024];
    char path[1024];

    read(fd,buffer,1024);
    strcat(buffer,"\0");

    char *str_end=strchr(buffer,'H');
    int str_i_end=str_end-buffer;

    char *str_begin=strchr(buffer,' ');
    int str_i_begin=str_begin-buffer+1;

    strncpy(path, buffer+str_i_begin+1,str_i_end-str_i_begin);
    path[str_i_end-str_i_begin-2]='\0';

    // send data
    struct stat file_stat;
    stat(path,&file_stat);

    printf("Path %s handled by thread %u\n",path,(int)pthread_self());
    int fp=open(path,O_RDONLY);
    if(fp==-1)
    {
        send_http(fd,0);
        write(fd,types[0],strlen(types[0]));
        write(fd,"<h1>FILE NOT FOUND</h1>",24);
    }
    else
    {
        send_http(fd,1);
       //identify content type
        char *data=(char *)malloc(sizeof(char)*file_stat.st_size);
        memset(data,0,sizeof(char)*file_stat.st_size);
        if(identify_file_type(path,fd)==1)
        {
            FILE *fp=popen("./temp.cgi","r");
            fread(data,file_stat.st_size,1,fp);
            fclose(fp);
        }
        else
        {
            int fp=open(path,O_RDONLY);
            read(fp,data,file_stat.st_size);
        }
        // write data
        write(fd,"\n",2);
        write(fd,data,file_stat.st_size);
        free(data);
    }
    close(fd);
    shutdown(fd,SHUT_RDWR);
    return ;

}

void sighandler(int signum)
{
    printf("Server shutdown\n");
    thpool_destroy(global);
    exit(0);
}

int main()
{
    signal(SIGINT, sighandler);
	printf("SERVER STARTED\t\t PORT : - %d\n",PORT);

    // create a pool of 10 threads
    threadpool thpool = thpool_init(TOT_POOL_THREADS);
    global=thpool;

	// create descriptors
	int sockfd,newsockfd;

	struct sockaddr_in server_address,client_address;
	socklen_t clilen;

	// create a socket
	sockfd = socket(AF_INET,SOCK_STREAM,0);
	if(sockfd < 0)
	{printf("Error creating socket\n");
		return -1;
	}

	// set all the content to zero
	bzero((char *)&server_address,sizeof(server_address));

	// setup address family of internet
	server_address.sin_family=AF_INET;

	server_address.sin_addr.s_addr = INADDR_ANY;
	server_address.sin_port=htons(PORT);	// htons = converts integer to bytes

	int bind_stat=bind(sockfd,(struct sockaddr *) &server_address, sizeof(server_address));
	if(bind_stat<0)
	{
		printf("Failed to bind to address ");
		return -1;
	}

	listen(sockfd,1000);	// handle atmost 100 connection in parallel;
	printf("Server listening\n");

	// accept the coonection
    clilen=sizeof(client_address);
    int cnt=0;
	while(1)
    {
        newsockfd=accept(sockfd, (struct sockaddr *) &client_address, &clilen);
        if(newsockfd<0)
        {
            printf("Failed to accept connection\n");
            return -1;
        }
        thpool_add_work(thpool,(void*)handle_request,(void*)&newsockfd);
    }
	close(sockfd);
	return 0;
}
