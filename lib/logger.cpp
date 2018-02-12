#include "logger.hpp"

Logger::Logger() {
    fptr = stderr;
}

Logger::Logger(char *fileName, char *mode) {
    fptr = fopen(fileName, mode);
}

void Logger::log(string logent) {
    time_t mytime;
    mytime = time(NULL);
    fprintf(fptr, "%.19s\t%s\n", ctime(&mytime), logent.c_str());
}