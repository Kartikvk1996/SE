CC=g++
CFLAGS= -std=c++11 -g -I ../../include/ ../../lib/proto/pdu.o ../../lib/server.o ../../lib/socket.o ../../include/proto/phashes/phash.o  -lpthread

tests: ../../lib/proto/pdu.o connect.test status.test config.test


../../lib/proto/pdu.o:
	+$(MAKE) -C ../../lib/

connect.test: connect.test.cpp
	$(CC) -o connect.test $^ $(CFLAGS)

status.test: status.test.cpp
	$(CC) -o status.test $^ $(CFLAGS)

config.test: config.test.cpp
	$(CC) -o config.test $^ $(CFLAGS)

.PHONY: clean

clean:
	rm *.test
