package se.ipc.pdu;

import java.lang.reflect.Field;
import jsonparser.DictObject;

public class ConnectPDU extends PDU {

    public int port;

    public ConnectPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.set(this, jObject.get(field.getName()).getValue());
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new InvalidPDUException();
            }
        }
    }
    
    public ConnectPDU(int port) {
        super(PDUConsts.METHOD_CONNECT);
        this.port = port;
    }

    public int getConnectPort() {
        return port;
    }

}
