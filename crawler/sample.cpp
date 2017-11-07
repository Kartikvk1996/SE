#include <bits/stdc++.h>
#include <curl/curl.h>
#include <iostream>
#include <fstream>
#include <string>
#include <ctime>

using namespace std;

struct urlInfo
{
	int urlID;			//Provided by Queue implementing team
	string url;
	string parsedTime;
	string nameOfFile;
	struct urlInfo *next;
};


class doWork
{
      public:
	void requestLinks(struct urlInfo ** , struct urlInfo **);       //Implement this function after knowing how "Crawler"(written by Kartik) sends URLs
	void generateHTMLPage(struct urlInfo ** , struct urlInfo **);
	void putItInFile(struct urlInfo **);
	
};

int main(void)
{
	doWork cwl;
	
	struct urlInfo * head = NULL;
	struct urlInfo * tail = NULL;

	cwl.requestLinks(&head , &tail);
	cwl.generateHTMLPage(&head , &tail);	
 
  	return 0;
}

void doWork :: generateHTMLPage(struct urlInfo ** head , struct urlInfo ** tail)
{
	CURL *curl;
  	CURLcode res;
 
  	curl_global_init(CURL_GLOBAL_DEFAULT);
 
        curl = curl_easy_init();
	
	while((*head) != NULL)
	{	
		struct urlInfo *nodeToBeDeleted = (*head);
		string url=(*head)->url;
		char u[100];
		strcpy( u , url.c_str() );
		
		
  		if(curl)
 		{
			FILE *fp=fopen("temp","w");
    			curl_easy_setopt(curl, CURLOPT_URL, u);
			curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);
			curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, 0L);			
			curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, NULL);//
        		curl_easy_setopt(curl, CURLOPT_WRITEDATA, fp);	    //
			res = curl_easy_perform(curl);
			//cout<<sizeof(res);	
    			if(res != CURLE_OK)
     			  fprintf(stderr, "curl_easy_perform() failed: %s\n",curl_easy_strerror(res));
			
			curl_easy_cleanup(curl);
  		}
		
		time_t now = time(0);
   		string dateTime = ctime(&now);
		(*head)->parsedTime = dateTime;
		
		doWork file;
		file.putItInFile((head)); 

  		curl_global_cleanup();
		
		(*head)=(*head)->next;
		delete(nodeToBeDeleted);
		
	}
	(*tail) = NULL;
		
}

void doWork :: requestLinks(struct urlInfo ** head , struct urlInfo ** tail)
{
	struct urlInfo *u;

	u = new(struct urlInfo); 
	if (u == NULL)
        {
		cout<<"Memory not allocated "<<endl;
		return;
	}

	string value = "https://www.wikipedia.org/";
	u->url = value;				//You can get URL from crawler(written by Kartik)
	u->urlID = 10;				//u->urlID = < call appropriate function writeen by Queue implementing team >

	ofstream f;
  	string fileName = "dummyNameofHTMLFile";//To get 'fileName' call getFileID() function written by Kartik 
	u->nameOfFile = "dummyNameofHTMLFile";	//u->nameOfFile = fileName;
	f.open( fileName.c_str() );		//File is created
	u->next = NULL;
	
	if((*head) == NULL)
	{
		(*head) = (*tail) = u;
		return;
	}
	 
	(*tail)->next = u;
	(*tail) = u;		
	
}

void doWork :: putItInFile(struct urlInfo ** currentNode)
{
	char nameOfFile[100];

	string fileName = (*currentNode)->nameOfFile;
	strcpy( nameOfFile , fileName.c_str() );
	ofstream file (nameOfFile);

  	if (file.is_open())
  	{
		file << "Name of File" 	<<"\t\t\t" << (*currentNode)->nameOfFile <<"\n";
    		file << "URL-ID      " 	<<"\t\t\t" << (*currentNode)->urlID      <<"\n";
		file << "URL         "  <<"\t\t\t" << (*currentNode)->url        <<"\n";
		file << "Parsed Time "  <<"\t\t\t" << (*currentNode)->parsedTime <<"\n";
   	 	file.close();
  	}
  	else 
	        cout << "Unable to open file";
  	return;
}	
