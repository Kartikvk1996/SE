/*  Following function define supported files
    and sends content type accordingly  */

#define TOT_FILE_TYPES 12

char types[10][50]={"Content-Type: text/html\r\n",
                     "Content-Type: text/plain\r\n",
                     "Content-Type: application/javascript\r\n",
                     "Content-Type: application/json\r\n",
                     "Content-Type: text/css\r\n",
                     "Content-Type: image/png\r\n",
                     "Content-Type: image/jpeg\r\n",
                     "Content-Type: image/gif\r\n",
                     "Content-Type: application/pdf\r\n",
                     };

int arr[]={     /*html*/    0,
                /*htm*/     0,
                /*js*/      2,
                /*json*/    3,
                /*cgi*/     0,
                /*c*/       1,
                /*cpp*/     1,
                /*css*/     4,
                /*png*/     5,
                /*jpeg*/    6,
                /*pdf*/     9,
                /*gif*/     8};


    char extentions[][20]={ ".html",
                            ".htm",
                            ".js",
                            ".json",
                            ".cgi",
                            ".c",
                            ".cpp",
                            ".css",
                            ".png",
                            ".jpeg",
                            ".pdf",
                            ".gif"};




int identify_file_type(char *path,int fd)
{
    int ittr=0;
    for(ittr=0;ittr<TOT_FILE_TYPES;ittr++)
    {
        if(strstr(path,extentions[ittr])!=NULL)
        {
            send(fd,types[arr[ittr]],strlen(types[arr[ittr]]),0);
            if(ittr==4)
            {
                return 1;       // if it is executable file
            }
            else
            {
                return 0;       // non executable file
            }

        }
    }
    send(fd,types[arr[1]],strlen(types[arr[1]]),0);
    return 0;
}

