CC=g++
CFLAGS=-c -std=c++11 -g

phash.o: phash.cpp methods.proto
	$(CC) generator.cpp -o generator
	$(CC) hashgen.cpp phash.cpp -o hashgenerator
	touch phashes.hpp
	rm phashes.hpp
	./generator \
		"METHOD" methods.proto		\
		"PC" processes.proto		\
		"GET" getattrs.proto		\
		"GUEST" guestattrs.proto	\
		"ERROR" errors.proto		\
		"CONNECT" connect.proto		\
		>> phashes.hpp
	$(CC) $(CFLAGS) $<
methods: 

.PHONY: clean
clean:
	rm hashgenerator generator phashes.hpp *.o
