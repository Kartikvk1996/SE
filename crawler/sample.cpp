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
	FILE *of;
};


class doWork
{
public:
	//Implement this function after knowing how "Crawler"(written by Kartik) sends URLs
	void requestLinks(struct urlInfo ** , struct urlInfo **);
	void getHTMLPage(struct urlInfo*);
	void printInfo(struct urlInfo *);
};

int main(void)
{
	doWork cwl;
	
	struct urlInfo * head = NULL;
	struct urlInfo * tail = NULL;

	cwl.requestLinks(&head , &tail);
  	return 0;
}

void doWork :: getHTMLPage(struct urlInfo *urlInfo)
{
	CURL *curl;
  	CURLcode res;
 
  	curl_global_init(CURL_GLOBAL_DEFAULT);
 	curl = curl_easy_init();
	
	if(curl) {
		
		printInfo(urlInfo);

		curl_easy_setopt(curl, CURLOPT_URL, urlInfo->url.c_str());
		curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);
		curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, 0L);
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, NULL);
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, urlInfo->of);
		res = curl_easy_perform(curl);
			
		if(res != CURLE_OK)
   			fprintf(stderr, "curl_easy_perform() failed: %s\n",curl_easy_strerror(res));
		
		curl_easy_cleanup(curl);
		curl_global_cleanup();

		time_t t = time(0);
		urlInfo->parsedTime = ctime(&t);
	}
}

void doWork :: requestLinks(struct urlInfo ** head , struct urlInfo ** tail)
{
	struct urlInfo *u = new(struct urlInfo); 
	
	u->url = "https://www.wikipedia.org/";
	u->urlID = 10;
	u->of = fopen("temp", "w");

	*head = u;
	*tail = NULL;

	for(urlInfo *u = *head; u != *tail; u = u->next) {
		getHTMLPage(u);
		printf("foo\n");
	}
}

void doWork :: printInfo(struct urlInfo *urlNode) {
	fprintf(
		urlNode->of,
		"Name of File \t\t\t %s\n"
		"URL-ID       \t\t\t %d\n"
		"URL          \t\t\t %s\n"
		"Parsed Time  \t\t\t %s\n\n",
		(urlNode)->nameOfFile.c_str(),
		(urlNode)->urlID,
		(urlNode)->url.c_str(),
		(urlNode)->parsedTime.c_str()
	);
}	
