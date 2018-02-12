CC=g++
CFLAGS= -c -std=c++11 -I ../include/

dmgr: dmgr.cpp reader.o writer.o  ../lib/proto/pdu.o ../lib/logger.o ../lib/reqhandler.o ../include/proto/phashes/phash.o ../lib/stream.o ../lib/server.o ../lib/socket.o
	$(CC) -I ../include/ -o dmgr -std=c++11 -g $^ -lpthread

reader.o: reader.cpp
	$(CC) $(CFLAGS) $<

writer.o: writer.cpp
	$(CC) $(CFLAGS) $<

../lib/logger.o:
../lib/server.o:
../lib/socket.o:
../lib/reqhandler.o:
../lib/pdu.o:
	+$(MAKE) -C ../lib/

../include/proto/phashes/phash.o:
	+$(MAKE) -C ../include/proto/phashes/

.PHONY: clean

clean:
	rm *.o dmgr
