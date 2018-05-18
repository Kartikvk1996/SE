#define URLLENGTH 512
#define ABSTRACT 128
#define TITLE 64

struct linkInfo
{
    unsigned long urlId;
    unsigned long checksum;
    double pgrank;
    unsigned int iLinks;
    unsigned int oLinks;
    unsigned long lastCrawled;
    unsigned char changeRate;
    unsigned int pageSize;
    char title[TITLE];
    char abstract[ABSTRACT];
    char url[URLLENGTH];
};
