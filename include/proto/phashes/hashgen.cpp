#include <stdio.h>
#include <ctype.h>
#include "phash.hpp"
void stoupper(char *s) {
    while(*s) {
        *s = toupper(*s);
        s++;
    }
}

int main(int argc, char **argv) {

    if(argc < 2) {
        printf("usage : %s <prefix>\n", argv[0]);
        return 0;
    }

    char buf[128];
    while(gets(buf) != NULL) {
        /* ignore comments */
        if(buf[0] == '#') {
            printf("\n//%s\n", buf);
            continue;
        }
        stoupper(buf);
        printf("#define %s%s\t(%d)\t//%s\n", argv[1], buf, phash(buf), buf);
    }
}
