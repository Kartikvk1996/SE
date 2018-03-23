/*
 * file: reqhanlder.hpp
 * Madhusoodan pataki [03-Jan-2018]
 *
 * This class is used whenever you want to implement the Servlet functionality.
 * Override the handle method in it and implement the service using the Socket 
 * provided to you. 
 *
 * **NOTE**
 *  Users may want to extend this class to get rid of creating another class. 
 */

//multi-include guard.
#ifndef REQ_HANDLER
#define REQ_HANDLER

#include "socket.hpp"

class ReqHandler {

public:
    /* override this method */
    virtual void handle(Socket *s);

};

#endif
