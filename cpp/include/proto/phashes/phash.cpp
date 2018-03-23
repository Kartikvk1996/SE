#include "phash.hpp"

hash_t phash(const char *str) {
    const hash_t mod = (hash_t)1e5;
    hash_t x = 0, i = 0;
    for( ; str[i]; ++i)
        x = ((x * 127) % mod + str[i]) % mod;
    return x;
}
