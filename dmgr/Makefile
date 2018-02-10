CC=g++
CFLAGS= -c -std=c++11 -I ../include/

dmgr: dmgr.cpp reader.o writer.o ../lib/linereader.o ../lib/proto/pdu.o ../lib/reqhandler.o 
	$(CC) -I ../include/ -o dmgr -std=c++11 -g $^ ../lib/server.o ../lib/socket.o ../include/proto/phashes/phash.o -lpthread

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