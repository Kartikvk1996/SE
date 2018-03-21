#include "pdu.hpp"

class ErrorPDU : public PDU {
public :
	ErrorPDU(int errcode, string error);
};
