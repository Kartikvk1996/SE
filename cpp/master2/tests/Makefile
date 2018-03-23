CC=g++
CFLAGS= -std=c++11 -g -I ../../include/ ../../lib/stream.o ../../lib/logger.o ../../lib/proto/pdu.o ../../lib/server.o ../../lib/socket.o ../../include/proto/phashes/phash.o ../../lib/node.o ../../lib/probable.o ../../lib/reqhandler.o ../../lib/proto/intropdu.o ../../lib/util.o  -lpthread

tests: ../../lib/proto/pdu.o connect.test status.test config.test intro.test


../../lib/proto/pdu.o:
	+$(MAKE) -C ../../lib/

intro.test: intro.test.cpp
	$(CC) -o intro.test $^ $(CFLAGS)

connect.test: connect.test.cpp
	$(CC) -o connect.test $^ $(CFLAGS)

status.test: status.test.cpp
	$(CC) -o status.test $^ $(CFLAGS)

config.test: config.test.cpp
	$(CC) -o config.test $^ $(CFLAGS)

.PHONY: clean

clean:
	rm *.test
