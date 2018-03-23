CC=g++
CFLAGS= -std=c++11 -g -I ../../include/
TFLAGS= -lpthread
LIBS= ../../lib/stream.o ../../lib/logger.o ../../lib/socket.o ../../lib/proto/pdu.o ../../include/proto/phashes/phash.o

tests: proto.test datarecv.test
	pwd

proto.test: proto.test.cpp $(LIBS)
	$(CC) $(CFLAGS) -o proto.test $^ $(TFLAGS)

datarecv.test: datarecv.test.cpp $(LIBS)
	$(CC) $(CFLAGS) -o datarecv.test $^ $(TFLAGS)

../../include/proto/phashes/phash.o:
	+$(MAKE) -C ../../include/proto/phashes/

../../lib/logger.o:
../../lib/stream.o:
../../lib/socket.o:
../../lib/proto/pdu.o:
	+$(MAKE) -C ../../lib/

.PHONY: clean

clean:
	rm proto.test
