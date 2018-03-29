package se.ipc.pdu;

import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class KillPDU extends PDU {
    
    @JsonExposed public int pid;
    
    public KillPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
    }
    
    public KillPDU(int pid) {
        super(PDUConsts.METHOD_KILL);
        this.pid = pid;
    }
    
    public int getPid() {
        return pid;
    }
}
