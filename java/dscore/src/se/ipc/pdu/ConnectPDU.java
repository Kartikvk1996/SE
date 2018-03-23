package se.ipc.pdu;


import java.util.Arrays;
import se.ipc.Consts;
import se.util.Logger;

public class ConnectPDU extends PDU {
    
    public static final String CONNECT_PORT = "CONNECT_PORT";
    
    public ConnectPDU(int port) {
        super(PDU.METHOD_CONNECT);
        try {
            setValue(Consts.jPath(PDU.DATA, CONNECT_PORT), port + "");
        } catch (JsonPathNotExistsException ex) {
            Logger.elog(Logger.HIGH, Arrays.toString(ex.getStackTrace()));
        }
    }
    
    public int getConnectPort() {
        return Integer.parseInt(
                getValue(Consts.jPath(PDU.DATA, ConnectPDU.CONNECT_PORT))
        );
    }
    
}
