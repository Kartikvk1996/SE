CC=g++
CFLAGS= -c -std=c++11

dmgr: dmgr.cpp linereader.o reader.o writer.o ../lib/pdu.o ../lib/reqhandler.o 
	$(CC) -o dmgr -std=c++11 -g $^ ../lib/server.o ../lib/socket.o ../lib/proto/phashes/phash.o -lpthread

linereader.o: linereader.cpp linereader.hpp
	$(CC) $(CFLAGS) $<

../include/util.o: ../include/util.cpp
	+$(MAKE) -C ../include/

reader.o: reader.cpp
	$(CC) $(CFLAGS) $<

writer.o: writer.cpp
	$(CC) $(CFLAGS) $<

../lib/reqhandler.o:
	+$(MAKE) -C ../lib/

../lib/pdu.o:
	+$(MAKE) -C ../lib/

.PHONY: clean

clean:
	rm *.o dmgr
