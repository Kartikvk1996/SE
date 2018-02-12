CC=g++
CFLAGS= -c -std=c++11 -g -I ../include/

master2: master.cpp slave.o ../lib/proto/pdu.o \
	 ../lib/reqhandler.o ../lib/server.o \
	 ../lib/socket.o ../include/proto/phashes/phash.o \
	 ../lib/stream.o ../lib/logger.o
	$(CC) -I ../include/ -o master2 -std=c++11 -g $^ -lpthread

../lib/logger.o:
../lib/server.o:
../lib/socket.o:
../include/proto/phashes/phash.o:
../lib/stream.o:
../lib/proto/pdu.o:
../lib/reqhandler.o:
	+$(MAKE) -C ../lib/

slave.o: slave.cpp slave.hpp
	$(CC) $< $(CFLAGS)

.PHONY: clean

clean:
	rm *.o master2
