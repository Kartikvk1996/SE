CC=g++
CFLAGS=-c

doit: pdu.o socket.o server.o
	+$(MAKE) -C ./proto/phashes/

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
