#ifndef UTIL_DEFINED
#define UTIL_DEFINED

using namespace std;

string ushort2str(ushort i) {
	char buf[16];
	sprintf(buf, "%d", i);
	return string(buf);
}

#endif
