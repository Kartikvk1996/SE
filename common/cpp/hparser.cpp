#include"../headers/hparser.hpp"


using HttpParser=__HTTP_PARSER__::HttpParser;

void HttpParser::reParseData(string data)
{
    parsableData=data;
    parseData();
}

void HttpParser::parseData()
{
    stringstream ss(parsableData);
	string data,key,value;
	getline(ss,data,' ');
	this->method=data;
	if(this->method=="GET")
	{
		getline(ss,data,' ');
		{
			stringstream fp(data);
			getline(fp,filePath,'/');
			getline(fp,filePath,'?');

			string GET_DATA;
			getline(fp,GET_DATA,' ');

			stringstream xx(GET_DATA);

			while(getline(xx,GET_DATA,'&')) // get key-value pair of get data
			{
				stringstream key_value(GET_DATA);
				getline(key_value,key,'=');
				getline(key_value,value,'=');
				GET.insert(make_pair(key,value));
			}

		}

		// getting the header fields
		while(getline(ss,data))
		{
			stringstream header(data);
			getline(header,key,':');
			getline(header,value);
			HEADERS.insert(make_pair(key,value));
		}
	}
	else if(this->method=="POST")
	{
		getline(ss,data,' ');
		{
			stringstream fp(data);
			getline(fp,filePath,'/');
		}

		getline(ss,data);
		string POST_DATA;
		// getting the header fields
		while(getline(ss,data))
		{
			stringstream header(data);
			getline(header,key,':');
			getline(header,value);
			HEADERS.insert(make_pair(key,value));
		}
		POST_DATA=key;
		HEADERS.erase(POST_DATA);

		stringstream xx(POST_DATA);

		while(getline(xx,POST_DATA,'&')) // get key-value pair of get data
		{
			stringstream key_value(POST_DATA);
			getline(key_value,key,'=');
			getline(key_value,value,'=');
			POST.insert(make_pair(key,value));
		}

	}
	else
	{
		std::runtime_error(Formatter()<<"["<<__func__<<"]\tError in parsing the HTTP request, neither GET nor POST method specified");
	}
	return ;
}

string HttpParser::_GET(string field)
{
    it=GET.find(field);
    if(it==GET.end())
    {
        return "NULL";
    }
    else
    {
        return it->second;
    }
}

string HttpParser::_POST(string field)
{
    it=POST.find(field);
    if(it==POST.end())
    {
        return "NULL";
    }
    else
    {
        return it->second;
    }
}

bool HttpParser::issetGET(string field)
{
    it=GET.find(field);
    if(it==GET.end())
    {
        return false;
    }
    else
    {
        return true;
    }
}

bool HttpParser::issetPOST(string field)
{
    it=POST.find(field);
    if(it==POST.end())
    {
        return false;
    }
    else
    {
        return true;
    }
}
