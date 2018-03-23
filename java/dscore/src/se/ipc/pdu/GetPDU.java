package se.ipc.pdu;

import se.ipc.Consts;

public class GetPDU extends PDU {
    
    public static final String RESOURCE_NAME = "RESOURCE";
    
    public GetPDU() {
        setMethod(PDU.METHOD_GET);
    }
    
    public String getResourceName() {
        return getValue(Consts.jPath(PDU.DATA, RESOURCE_NAME));
    }
}
