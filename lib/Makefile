CC=g++
CFLAGS=-c -g -std=c++11

doit: pdu.o socket.o server.o reqhandler.o
	+$(MAKE) -C ./proto/phashes/

reqhandler.o: reqhandler.cpp reqhandler.hpp
	$(CC) $(CFLAGS) $<

pdu.o: ./proto/pdu.cpp ./proto/pdu.hpp
	+$(MAKE) -C ./proto/phashes/
	$(CC) $(CFLAGS) $<

socket.o: socket.cpp socket.hpp
	$(CC) $(CFLAGS) $<

server.o: server.cpp server.hpp
	$(CC) $(CFLAGS) $<

.PHONY: clean
clean:
	rm *.o
