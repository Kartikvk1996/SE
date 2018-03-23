/* Functions checks , data has been sent or not */
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

