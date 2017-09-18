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
#include<assert.h>


#include"ThreadPool/thpool.h"
#include"http.h"
#include"file_support.h"
#include"handle_request.h"
#include"config.h"

threadpool global;

void sighandler(int signum)
{
    printf("Server shutdown\n");
    thpool_destroy(global);
    exit(0);
}

int main()
{
    signal(SIGINT, sighandler);

    config_server();    // configure the server

	printf("\n\n\n\t\tSERVER STARTED\t PORT : - %d\n",PORT);
    printf("\t\tMAX CONCURRENT CONNECTIONS : - %d\n",TOTAL_CONNECTIONS);
    printf("\t\tTHREAD POOL : - %d\n",TOTAL_THREADS);

    // create a pool of 10 threads
    threadpool thpool = thpool_init(TOTAL_THREADS);
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

	listen(sockfd,TOTAL_CONNECTIONS);	// handle atmost 100 connection in parallel;

	// accept the coonection
    clilen=sizeof(client_address);
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
