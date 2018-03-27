package se.ipc.pdu;

import java.lang.reflect.Field;
import jsonparser.DictObject;

public class ConnectPDU extends PDU {

    public int port;
    public SysInfo sysInfo;
    
    public ConnectPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
    }
    
    public SysInfo getSysInfo() {
        return sysInfo;
    }

    public ConnectPDU(int port) {
        super(PDUConsts.METHOD_CONNECT);
        this.sysInfo = new SysInfo(true);
        this.port = port;
    }

    public int getConnectPort() {
        return port;
    }

}
