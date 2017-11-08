#include <stdio.h>
#include <ctype.h>
#include "phash.hpp"
void stoupper(char *s) {
    while(*s) {
        *s = toupper(*s);
        s++;
    }
}

int main() {
    char buf[128];
    while(scanf("%s", buf) > 0) {
        stoupper(buf);
        printf("#define METHOD_%s\t(%d)\t//%s\n", buf, phash(buf), buf);
    }
}
