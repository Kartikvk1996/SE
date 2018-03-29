package se.ipc.pdu;

import java.lang.reflect.Field;
import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class IntroPDU extends PDU {

    @JsonExposed public String host;
    @JsonExposed public int port;
    
    public IntroPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.set(this, jObject.get(field.getName()).getValue());
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new InvalidPDUException();
            }
        }
    }
    
    
    public IntroPDU(String host, int port) {
        super(PDUConsts.METHOD_INTRO);
        this.host = host;
        this.port = port;
    }
    
    public String getGuestHost() {
        return host;
    }
    
    public int getGuestPort() {
        return port;
    }
    
}
