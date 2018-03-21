CC=g++
CFLAGS = -c -std=c++11 -g -I ../../include/

compile: pdu.o intropdu.o errpdu.o
	pwd

errpdu.o: errpdu.cpp
	$(CC) $(CFLAGS) $<

intropdu.o: intropdu.cpp
	$(CC) $(CFLAGS) $<

pdu.o: pdu.cpp
	+$(MAKE) -C ../../include/proto/phashes/
	$(CC) $(CFLAGS) $< 

.PHONY: clean
clean: 
	rm *.o
