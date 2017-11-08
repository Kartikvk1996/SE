#include "../lib/json.hpp"
using json = nlohmann::json;

#include <thread>
#include "masterconfig.hpp"
#include "../lib/server.hpp"
#include "../lib/connection.hpp"

#include "../lib/proto/phashes.hpp"

void handle_request(Socket *s);

class Master
{
public:
	MasterConfig *config;
	Server *mserver;
	
	Master(string host, ushort port) {
		mserver = new Server(host, port, handle_request);
	}
	
	void run() {
		mserver->run();
	}
};

void handle_connect(json &req) {
	
}

void handle_get(json &req) {

}

void handle_update(json &req) {

}

void handle_ack(json &req) {

}

void handle_request(Socket *s) {
	string str = s->readData();
	json req = json::parse(str);
	const char *cstr = req["method"].get<string>().c_str();
	cout << cstr << endl;
	switch(phash(cstr)) {
		case METHOD_CONNECT:
			handle_connect(req);
			break;
		case METHOD_GET:
			handle_get(req);
			break;
		case METHOD_UPDATE:
			handle_update(req);
			break;
		case METHOD_ACK:
			handle_ack(req);
			break;
	}
}

int main(int argc, char *argv[]) {
	Master master(argv[1], atoi(argv[2]));
	master.run();
}
