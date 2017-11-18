CC=g++
CFLAGS=-c

pdu.o: pdu.cpp pdu.hpp
	+$(MAKE) -C ./phashes/
	$(CC) $(CFLAGS) $< 

.PHONY: clean
clean: 
	rm *.o