CC=g++
CFLAGS=-c

phash.o: phash.cpp methods
	$(CC) hashgen.cpp phash.cpp -o hashgenerator
	echo "#ifndef PHASHES\n#define PHASHES\n" > phashes.hpp
	./hashgenerator < methods >> phashes.hpp
	echo "\n#endif" >> phashes.hpp
	$(CC) $(CFLAGS) $< 

methods: 

.PHONY: clean
clean:
	rm hashgenerator phashes.hpp *.o