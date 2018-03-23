binaries: precompile group1 group2
	echo "--done--"

precompile:
	rm bin/* -rf

group1: 
	echo "Nothing added from group1"

group2:ffc dmgr master2
	echo "Compiling group2 binaries."
	+$(MAKE) -C lib/
	+$(MAKE) -C ffc/
	+$(MAKE) -C dmgr/
	cp dmgr/dmgr bin/
	+$(MAKE) -C master2/
	cp master2/master2 bin/
	cp fireup/dist/lib -r bin/
	cp fireup/dist/fireup.jar bin/

.PHONY: clean

clean:
	+$(MAKE) clean -C ffc/
	+$(MAKE) clean -C dmgr/
	+$(MAKE) clean -C master2/
	+$(MAKE) clean -C lib/
