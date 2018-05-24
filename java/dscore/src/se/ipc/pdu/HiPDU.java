package se.ipc.pdu;

import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class HiPDU extends PDU {
    
    @JsonExposed(comment = "the port on which you are listening")
    public int port;
    
    @JsonExposed(comment = "the http port if you have that")
    public int httpPort;
    
    public HiPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
    }
    
    public HiPDU(int pPort, int httpPort) {
        super(PDUConsts.METHOD_HI);
        this.port = pPort;
        this.httpPort = httpPort;
    }
    
    public int getRunningPort() {
        return port;
    }
    
    public int getHttpPort() {
        return httpPort;
    }
}
