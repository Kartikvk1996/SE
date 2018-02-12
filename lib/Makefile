CC=g++
CFLAGS=-c -g -std=c++11 -I ../include/

doit: pdu.o socket.o server.o reqhandler.o linereader.o logger.o stream.o
	pwd

logger.o: logger.cpp
	$(CC) $(CFLAGS) $<

stream.o: stream.cpp
	$(CC) $(CFLAGS) $<

reqhandler.o: reqhandler.cpp
	$(CC) $(CFLAGS) $<

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
