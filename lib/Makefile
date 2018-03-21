CC=g++
CFLAGS=-c -g -std=c++11 -I ../include/

doit: pdu.o socket.o server.o reqhandler.o linereader.o logger.o stream.o probable.o node.o util.o errpdu.o
	pwd

util.o: util.cpp
	$(CC) $(CFLAGS) $<

probable.o: probable.cpp
	$(CC) $(CFLAGS) $<

node.o: node.cpp
	$(CC) $(CFLAGS) $<

logger.o: logger.cpp
	$(CC) $(CFLAGS) $<

stream.o: stream.cpp
	$(CC) $(CFLAGS) $<

reqhandler.o: reqhandler.cpp
	$(CC) $(CFLAGS) $<

errpdu.o:
pdu.o: ./proto/
	+$(MAKE) -C ./proto/

socket.o: socket.cpp stream.o
	$(CC) $(CFLAGS) $< 

server.o: server.cpp
	$(CC) $(CFLAGS) $<

linereader.o: linereader.cpp
	$(CC) $(CFLAGS) $<

.PHONY: clean
clean:
	rm *.o
