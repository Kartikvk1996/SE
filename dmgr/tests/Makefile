CC=g++
CFLAGS= -std=c++11 -g -I ../../include/
TFLAGS= ../../lib/server.o ../../lib/socket.o ../../include/proto/phashes/phash.o -lpthread

tests: proto.test

proto.test: proto.test.cpp ../../lib/proto/pdu.o
	$(CC) $(CFLAGS) -o proto.test $^ $(TFLAGS)

../../lib/pdu.o:
	+$(MAKE) -C ../../lib/

.PHONY: clean

clean:
	rm proto.test
