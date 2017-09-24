#include<iostream>
#include<string>
#include<stdlib.h>
#include<unistd.h>
#include<sys/types.h>
#include<stdio.h>
#include<string.h>


using namespace std;

int main()
{

        FILE *fr=fopen("links.txt","r");
        if(fr==NULL)
        {
                cout<<"Error\n";        exit(EXIT_FAILURE);
        }

        char path[1000];
        fscanf(fr,"%s",path);


        while(1)
        {
                char exec[1000]="curl ";
                char link[1000];

                char filter[1000]=" | grep -Po '(?<=href=\")[^\"]*' >> links.txt";


                fscanf(fr,"%s",link);
                if(link[0]=='/')
                {
                        strcat(path,link);      // append initial link
                        strcat(exec,path);
                        strcat(exec,filter);    // attach filter
                }
                else
                {
                        strcat(exec,path);
                        strcat(exec,filter);
                }

                FILE *fp=popen(exec,"r");
                if(fp==NULL)
                {
                        cout<<"Failed in getting data \n";
                        exit(EXIT_FAILURE);
                }
                pclose(fp);
        }
        return 0;
}
