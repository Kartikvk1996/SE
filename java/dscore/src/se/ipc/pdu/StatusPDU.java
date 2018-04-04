package se.ipc.pdu;

import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class StatusPDU extends PDU {

    @JsonExposed
    public static String ticket = "";

    @JsonExposed
    public static String pid = "";

    public StatusPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
    }
    
    public String getTicket() {
        return ticket;
    }
    
    public String getPid() {
        return pid;
    }

    public StatusPDU() {
        super(PDUConsts.METHOD_STATUS);
    }
    
    public static void setProcessDetails(String ticket, String pid) {
        StatusPDU.ticket = ticket;
        StatusPDU.pid = pid;
    }
}
