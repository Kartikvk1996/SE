package se.ipc.pdu;

import se.ipc.Consts;

public class ConnectPDU extends PDU {
    
    public static final String CONNECT_PORT = "CONNECT_PORT";
    
    public ConnectPDU() {
        super(PDU.METHOD_CONNECT);
    }
    
    public int getConnectPort() {
        return Integer.parseInt(
                getValue(Consts.jPath(PDU.DATA, ConnectPDU.CONNECT_PORT))
        );
    }
    
}
