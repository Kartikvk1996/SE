#ifndef PHASHES
#define PHASHES


//#These are the attributes used in CONNECT PDU

#define CONNECT_S_PORT	"PORT"//string PORT
#define CONNECT_PORT	(55329)	//PORT

//#These are the attributes of the error PDU

#define ERROR_S_CODE	"CODE"//string CODE
#define ERROR_CODE	(24557)	//CODE
#define ERROR_S_MSG	"MSG"//string MSG
#define ERROR_MSG	(52545)	//MSG

//#These are the attributes for intropdu

#define GUEST_S_HOST	"HOST"//string HOST
#define GUEST_HOST	(68392)	//HOST
#define GUEST_S_PORT	"PORT"//string PORT
#define GUEST_PORT	(55329)	//PORT

//#These are the attributes used by GET method

#define GET_S_RESOURCE	"RESOURCE"//string RESOURCE
#define GET_RESOURCE	(15918)	//RESOURCE

//# These are some hashes for identification of the process types.

#define PC_S_FFC	"FFC"//string FFC
#define PC_FFC	(37987)	//FFC
#define PC_S_CRAWLER	"CRAWLER"//string CRAWLER
#define PC_CRAWLER	(21172)	//CRAWLER
#define PC_S_RGEN	"RGEN"//string RGEN
#define PC_RGEN	(21406)	//RGEN
#define PC_S_WRITER	"WRITER"//string WRITER
#define PC_WRITER	(94611)	//WRITER
#define PC_S_READER	"READER"//string READER
#define PC_READER	(22115)	//READER
#define PC_S_DMGR	"DMGR"//string DMGR
#define PC_DMGR	(41076)	//DMGR
#define PC_S_WS	"WS"//string WS
#define PC_WS	(11132)	//WS
#define PC_S_GUEST	"GUEST"//string GUEST
#define PC_GUEST	(5592)	//GUEST

//#These are the methods used in PDU

#define METHOD_S_CONNECT	"CONNECT"//string CONNECT
#define METHOD_CONNECT	(44682)	//CONNECT
#define METHOD_S_CREATE	"CREATE"//string CREATE
#define METHOD_CREATE	(8380)	//CREATE
#define METHOD_S_GET	"GET"//string GET
#define METHOD_GET	(54006)	//GET
#define METHOD_S_UPDATE	"UPDATE"//string UPDATE
#define METHOD_UPDATE	(20041)	//UPDATE
#define METHOD_S_KILL	"KILL"//string KILL
#define METHOD_KILL	(15870)	//KILL
#define METHOD_S_WRITE	"WRITE"//string WRITE
#define METHOD_WRITE	(39327)	//WRITE
#define METHOD_S_ACK	"ACK"//string ACK
#define METHOD_ACK	(56969)	//ACK
#define METHOD_S_STATUS	"STATUS"//string STATUS
#define METHOD_STATUS	(21234)	//STATUS
#define METHOD_S_INTRO	"INTRO"//string INTRO
#define METHOD_INTRO	(97996)	//INTRO
#define METHOD_S_ERROR	"ERROR"//string ERROR
#define METHOD_ERROR	(80328)	//ERROR
#endif
