CC=g++
CFLAGS= -c -std=c++11 -g -I ../include/

master2: master.cpp masterconfig.o slaveconfig.o ../lib/proto/pdu.o ../lib/reqhandler.o
	$(CC) -I ../include/ -o master2 -std=c++11 -g $^ ../lib/server.o ../lib/socket.o ../include/proto/phashes/phash.o -lpthread

../lib/proto/pdu.o:
../lib/reqhandler.o:
	+$(MAKE) -C ../lib/

masterconfig.o: masterconfig.cpp masterconfig.hpp slaveconfig.hpp
	$(CC) $< $(CFLAGS)

slaveconfig.o: slaveconfig.cpp slaveconfig.hpp
	$(CC) $< $(CFLAGS)

.PHONY: clean

clean:
	rm *.o master2
