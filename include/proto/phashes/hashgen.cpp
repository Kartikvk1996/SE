#include <stdio.h>
#include <ctype.h>
#include "phash.hpp"
void stoupper(char *s) {
    while(*s) {
        *s = toupper(*s);
        s++;
    }
    if(*(--s) == '\n')
	*s = '\0';
}

int main(int argc, char **argv) {

    if(argc < 2) {
        printf("usage : %s <prefix>\n", argv[0]);
        return 0;
    }

    char buf[128];
    while(fgets(buf, sizeof(buf), stdin) != NULL) {
        /* ignore comments */
        if(buf[0] == '#') {
            printf("\n//%s\n", buf);
            continue;
        }
        stoupper(buf);
        printf("#define %sS_%s\t\"%s\"//string %s\n", argv[1], buf, buf, buf);
        printf("#define %s%s\t(%d)\t//%s\n", argv[1], buf, phash(buf), buf);
    }
}
