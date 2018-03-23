#include "logger.hpp"

Logger::Logger() {
    fptr = stderr;
}

Logger::Logger(char *fileName, char *mode) {
    fptr = fopen(fileName, mode);
}

void Logger::log(string type, string logent) {
    time_t mytime;
    mytime = time(NULL);
    fprintf(fptr, "%.19s\t%s\t%s\n", ctime(&mytime), type.c_str(), logent.c_str());
}

void Logger::ilog(string logent) {
    log("INFO", logent);
}

void Logger::elog(string logent) {
    log("ERROR", logent);
}