// This function handles the request from the user

#define BUFFER_SIZE 1024



void handle_request(void *fd_ptr)
{
    int fd=*((int *)fd_ptr);
    char buffer[BUFFER_SIZE];
    char path[BUFFER_SIZE];

    read(fd,buffer,BUFFER_SIZE);
    strcat(buffer,"\0");


    // following code gets the path from the http header
    /* ------------------- start --------------------*/
    char *str_end=strchr(buffer,'H');
    int str_i_end=str_end-buffer;

    char *str_begin=strchr(buffer,' ');
    int str_i_begin=str_begin-buffer+1;

    strncpy(path, buffer+str_i_begin+1,str_i_end-str_i_begin);
    path[str_i_end-str_i_begin-2]='\0';


    /* ------------------ end ----------------------*/


    /* open the file and send the data */
    struct stat file_stat;
    stat(path,&file_stat);

    printf("Path %s handled by thread %u\n",path,(int)pthread_self());

    int fp=open(path,O_RDONLY); // opens file in read mode
    if(fp==-1)                  // if file is not found
    {
        send_http(fd,0);
        write(fd,types[0],strlen(types[0]));
        write(fd,"<h1>FILE NOT FOUND</h1>",24);
    }
    else
    {
        send_http(fd,1);


        char *data=(char *)malloc(sizeof(char)*file_stat.st_size);
        memset(data,0,sizeof(char)*file_stat.st_size);


        /*  following code identifies the file type and sends mime accordingly
            if the file ends with .cgi it is considered as executable file      */
	size_t len=0;
        if(identify_file_type(path,fd)==1)
        {
            char tmp_path[BUFFER_SIZE]="./";
            strcat(tmp_path,path);
            FILE *fp=popen(tmp_path,"r");
	    fseek(fp,0L,SEEK_END);
	    len=ftell(fp);
	    rewind(fp);
	    printf("data %d",len);
            fread(data,len+1,1,fp);
            pclose(fp);
        }
        else
        {
            int fp=open(path,O_RDONLY);
            read(fp,data,file_stat.st_size);
        }
        // write data
        write(fd,"\n",2);                   // flush the stream
        write(fd,data,len+1);	    // send the data
	free(data);
    }
    close(fd);
    shutdown(fd,SHUT_RDWR);
    return ;

}
