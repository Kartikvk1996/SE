# using g++ compiler
CC=g++

#obj flags
OFLAGS=-Wall -Werror -fPIC -c

#compiler flags
CFLAGS=-Wall -Werror -g
VER=-std=c++14

#path where shared libraries are generated
LPATH=lib
#path where object files are generated
OPATH=obj
#path where source files are loacted
SPATH=cpp/

#executable file name
EXEC=master

PWD=${CURDIR}
LNKPATH=-L/$(PWD)/lib
CRAWLER_LNKPATH_COMM=-L/$(PWD)/../common/lib

default:executable
	@echo Build completed.

#creating libraries to generate executable
executable:genobjs genlbs
	@echo Executable file generated successfully : set LD_LIBRARY_PATH to $(PWD)/lib folder
	

genobjs:
	@echo Generating object files
	@for file in configure master  ; do \
		$(CC) $(VER) -fsanitize-recover=undefined $(OFLAGS) $(SPATH)/$$file.cpp -o $(OPATH)/$$file.o ; \
		echo $$file.o generated successfully at /$(OPATH)/ ; \
		echo ; \
		echo --------------------------------------------------------------- ; \
	done
	@echo 
	@echo
genlbs:
	@echo Generating library files
	@for file in configure master ; do \
		$(CC) $(VER) -shared $(OPATH)/$$file.o -o $(LPATH)/lib$$file.so ; \
		echo lib$$file.so generated successfully at /$(LPATH)/ ; \
		echo ; \
		echo --------------------------------------------------------------- ; \
	done
	
genexec:
	echo Generating executable files
	@$(CC) $(VER) -fsanitize-recover=undefined -O1 -g $(SPATH)/runner.cpp -o $(EXEC) -lpthread $(LNKPATH) -lconfigure -lmaster $(CRAWLER_LNKPATH_COMM) -lconnection -lhparser -llogger -lqueue
	

clean:
	rm genobjs
	rm genlbs
	rm executable
	rm default