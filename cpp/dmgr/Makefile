CC=g++
CFLAGS= -c -std=c++11 -I ../include/

dmgr: dmgr.cpp ../lib/proto/intropdu.o ../lib/proto/errpdu.o ../lib/node.o ../lib/probable.o  ../lib/proto/pdu.o ../lib/logger.o ../lib/reqhandler.o ../include/proto/phashes/phash.o ../lib/stream.o ../lib/server.o ../lib/socket.o ../lib/util.o
	$(CC) -I ../include/ -o dmgr -std=c++11 -g $^ -lpthread

writer.o: writer.cpp
	$(CC) $(CFLAGS) $<

../lib/proto/errpdu.o:
../lib/proto/intropdu.o:
../lib/util.o:
../lib/node.o:
../lib/probable.o:
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
	rm dmgr
