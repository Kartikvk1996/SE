#include "proto/errpdu.hpp"

ErrorPDU::ErrorPDU(int errcode, string error) : PDU() {
	jdata[ERROR_MSG] = error;
	jdata[ERROR_CODE] = errcode;
}

