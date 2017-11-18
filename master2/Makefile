CC=g++
CFLAGS= -c

master2: master.cpp masterconfig.o slaveconfig.o ../lib/pdu.o
	$(CC) -o master2 $^ ../lib/server.o ../lib/socket.o ../lib/proto/phashes/phash.o -lpthread

../lib/pdu.o:
	+$(MAKE) -C ../lib/

masterconfig.o: masterconfig.cpp masterconfig.hpp slaveconfig.hpp
	$(CC) $< $(CFLAGS)

slaveconfig.o: slaveconfig.cpp slaveconfig.hpp
	$(CC) $< $(CFLAGS)

.PHONY: clean

clean:
	rm *.o master2