/*
    Following codes convey specific messages
*/
#define REQUEST 1000
#define RESPONSE 2000


/*  Message codes for CRAWLER MANAGING SYSTEM(CMS)   */
#define CMS_REQ_NLINKS 1    // CMS requesting for new links
#define CMS_POLL       2    // CMS polling its crawlers to check their availability

#define CMS_SND_NLINKS_SUCC 3   // CMS sends links successfully to particular crawler
#define CMS_SND_NLINKS_ZERO -3  // No new links to send
#define CRS_POLL_ALIVE  4   //  Response from Crawler
#define CMS_SHUTDOWN 5      // Master shutting down

/*  Message codes for CONTEXT RETRIEVAL SYSTEMS (CRS)   */
#define CRS_CONNECT 5   // CRS send connect request to CMS
#define CRS_CONN_LMTEXC -5 // max limit exceed
#define CRS_CONN_SUCC   6
#define CRS_DISCONNECT 7    //  CRS disconnects from Master
#define CRS_REQ_NLINKS 8    //  Crawler Requests for new links
#define SND_NLINKS_SUCC 9   //  Data Server sends new links to master
#define CRS_UPDT_LINKS  10   // Send update link information
#define CRS_STATE_UPDT  11   // Report unreported previous state
#define CRS_SHUTDOWN    12   // Shutting down of Crawler


#define SND_NLINKS_ZERO 13   // Dataserver had no links to send



#define NEW_URLS 20
#define GET_URLS 21
#define CRSMASTER_POLL_ALIVE 22
#define DS_POLL 23
#define DS_CONNECT_SUCC 24
#define CRSMASTER_CONNECT 25


/* ------------URL results------------*/
#define URL_RESULTS 50
