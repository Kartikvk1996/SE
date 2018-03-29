package se.ipc.pdu;

import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class KillPDU extends PDU {
    
    @JsonExposed public String pid;
    
    public KillPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
    }
    
    public KillPDU(String pid) {
        super(PDUConsts.METHOD_KILL);
        this.pid = pid;
    }
    
    public String getPid() {
        return pid;
    }
}
