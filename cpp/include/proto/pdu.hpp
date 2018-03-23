/*
 * file: [pdu.hpp]
 * 
 * contributors:
 * 		Anarghya K Jantali	[07 Nov 2017]
 * 		Madhusoodan M Pataki [08 Nov 2017]
 *
 * Declares some method-types, and a wrapper class to build the PDU.
 * 
 */
#ifndef PDU_INCLUDED
#define PDU_INCLUDED

#include <iostream>
#include <string>
#include "json.hpp"
#include "./phashes.hpp"

using namespace std;
using json = nlohmann::json;

#define WHO 			"WHO"
#define WHOM 			"WHOM"
#define DATA			"DATA"
#define METHOD			"METHOD"

extern string PROCESS_ROLE;

class PDU
{
private:
	void setWho(string pname);	//capitalizes things
protected:
	string method;
	string who;
	string data;

	/* this is json version of data it will be set when you parse
	 * the data from the string. */
	json jdata;			

public:
	PDU(int method);

	PDU();

	PDU(string &jsonString);

	string getMethod();

	void setMethod(int pmethod);

	/* separate function to load data/PDU*/
	void setData(string bufferedData);

	void setJData(json jobj);

	json getJSON();

	string toString();

	string getData();

	string getSenderType();

	json getDataAsJson();
};

#endif
