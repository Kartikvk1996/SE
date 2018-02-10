CC=g++
CFLAGS = -c -std=c++11 -g -I ../../include/

pdu.o: pdu.cpp
	+$(MAKE) -C ../../include/proto/phashes/
	$(CC) $(CFLAGS) $< 

.PHONY: clean
clean: 
	rm *.o
