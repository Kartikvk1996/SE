
/*
DataManager class,instantiates datamanagers
Each Datamanager is associated with a range/category determined by FFC module
provides primarily:
1.Reverse-Indexing keywords (build table: keyword-> document)
2.Receive query-keywords
3.Write table to file/disk 
4.Write content to disk

*/

#include<iostream>
#include<string>
#include<json.hpp>
#define MAX_SIZE 4096
#define MAX_PENDING 400
using namespace std;
using json = nlohmann::json;


struct Content{
//blah blah
//bleh bleh bleh

};
class dataManager{

private:
    int ID;//unique ID for each DMGR,assigned by FFC
    int lowerValue,upperValue;//category bounds
    int pendingRequests=0;
    vector<Content> receivedContent;
    int maxChunkSize=MAX_SIZE;
    int chunkSize=0;//block size for writing
    
public:
    //constructor provides creation and sets up necessary values for individual DMGR
    dataManager(int lowerThreshold,int upperThreshold,int id){
        lowerValue=lowerThreshold;
        upperValue=upperThreshold;
        ID=id;

    }
    //FFC sends writeRequest to corresponging DMGRs
    bool receiveWriteRequest(json writeRequest){
        ++pendingRequests;
        Content newwebContent=new Content();
        

/*
//dump json data to web content

*/

        if(pendingRequests>MAX_PENDING){
            if(updateTable()&&writeToDisk())pendingRequests=0;
            
        }

       if( updateTable()&&writeToDisk())
            
        return true;
        return false;
    }

    //resize block/chunk size to optimize IO performance
    bool resizeChunk(int size){maxChunkSize=size;}

private:
/*
dump content to disk
This is a generic method which will write any content, be it web content or hashTables
Invoked only after minimum updates/write requests
*/
bool writeToDisk( ){

        if(flushChunk())return true;return false;

    }

    
    //This flushes internal buffer
    bool flushChunk(){
        return true;
    }


        bool updateTable(){
            for(vector<Content> c:receivedContent){
                    vector<string>strings;


            }

        }

        
            


        }





};