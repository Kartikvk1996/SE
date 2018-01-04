CC=g++
CFLAGS= -c -std=c++11 -g
TFLAGS= ../../lib/server.o ../../lib/socket.o ../../lib/proto/phashes/phash.o -lpthread


tests: prototest

prototest: proto.test.cpp ../../lib/pdu.o
	$(CC) -o proto.test $^ $(TFLAGS)

../../lib/pdu.o:
	+$(MAKE) -C ../../lib/

.PHONY: clean

clean:
	rm *.o master2.*.test
