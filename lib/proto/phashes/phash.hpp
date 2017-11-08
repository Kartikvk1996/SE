int phash(const char *str) {
    const int mod = (int)1e5;
    int x = 0, i = 0;
    for( ; str[i]; ++i)
        x = ((x * 127) % mod + str[i]) % mod;
    return x;
}
