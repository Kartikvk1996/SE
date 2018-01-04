CC=g++
CFLAGS = -c -std=c++11 -g

pdu.o: pdu.cpp pdu.hpp
	+$(MAKE) -C ./phashes/
	$(CC) $(CFLAGS) $< 

.PHONY: clean
clean: 
	rm *.o
