#!/bin/bash

declare -a headers=("configure.hpp" "crawler.hpp" "http_parser.hpp" "http_server.hpp" "master.hpp" "master_init.hpp")
declare -a libraries=("connection.hpp" "ctpl_stl.h" "json.hpp" "logger.hpp" "msgcodes.hpp" "mut_locks.hpp" "systeminfo.hpp")


echo "Searching for all required headers"
for header in ${headers[@]}
do
	cmd="$(find -name $header)"
	if [ "$cmd" != "" ] ; then
		echo "$header found"
	else
		echo "$header not found"
		exit
	fi
done

echo 
echo "-------------------------------------------------------------------------"
echo 

echo "Searching for all required lib files"
for library in ${libraries[@]}
do
        cmd="$(find ../lib/ -name $library)"
        if [ "$cmd" != "" ] ; then
                echo "$library found"
        else
                echo "$library not found"
                exit
        fi
done

echo
echo "-------------------------------------------------------------------------"
echo

res="$(dpkg -s g++)"

echo "${res}"

if [ "${res}" != "" ] ; then
	echo "GCC Compiler found"
else
	echo "GCC Compiler not found"
	exit
fi

echo
echo "-------------------------------------------------------------------------"
echo


echo "compiling the program"

cmd="$(g++ -g -rdynamic runner.cpp -o runner -lpthread -std=c++14)"



