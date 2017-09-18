/*  Function to configure server port and total threads
    and number of connections it can handle concurrently
*/


/* ------- fields ------- */
int PORT;
int TOTAL_THREADS;
int TOTAL_CONNECTIONS;

int FIELDS=3;

void config_server()
{
    char *buff=(char *)malloc(sizeof(char)*30);
    assert(buff!=NULL);


    FILE *fp=fopen("config.txt","r");
    if(fp==NULL)
    {
        printf("\n\n\t\tconfig.txt file not found\n\t\tServer stopped\n\n\n");
        exit(EXIT_FAILURE);
    }
    else
    {
        int ittr=0;
        int arr[FIELDS+1];
        while(!feof(fp))
        {
            fscanf(fp,"%s",buff);
            fscanf(fp,"%s",buff);
            arr[ittr++]=atoi(buff);
        }

        PORT = arr[0];
        TOTAL_THREADS = arr[1];
        TOTAL_CONNECTIONS = arr[2];
    }
    fclose(fp);
    free(buff);
    return;
}
