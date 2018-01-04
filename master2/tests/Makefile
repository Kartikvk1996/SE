CC=g++
CFLAGS= -c -std=c++11 -g
TFLAGS= ../../lib/server.o ../../lib/socket.o ../../lib/proto/phashes/phash.o -lpthread


tests: connecttest statustest configtest

connecttest: connect.test.cpp ../../lib/pdu.o
	$(CC) -o connect.test $^ $(TFLAGS)

statustest: status.test.cpp ../../lib/pdu.o
	$(CC) -o status.test $^ $(TFLAGS)

configtest: config.test.cpp ../../lib/pdu.o
	$(CC) -o config.test $^ $(TFLAGS)

../../lib/pdu.o:
	+$(MAKE) -C ../../lib/

.PHONY: clean

clean:
	rm *.o master2.*.test
