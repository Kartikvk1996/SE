#include <stdio.h>
#include <stdlib.h>
int main(int argc, char **argv) {

	char buf[256];

	puts("#ifndef PHASHES\n#define PHASHES\n");
	fflush(stdout);

	while(--argc > 1) {
		sprintf(buf, "./hashgenerator \"%s_\" < %s", argv[argc-1], argv[argc]);
		system(buf);
		argc--;
	}

	puts("#endif");
}

