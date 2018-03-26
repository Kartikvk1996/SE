package se.ipc.pdu;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsonparser.DictObject;
import se.ipc.Consts;

public class GetPDU extends PDU {
    
    public String resource;
    
    public GetPDU(String resource) {
        super(PDUConsts.METHOD_GET);
        this.resource = resource;
    }
    
    public GetPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.set(this, jObject.get(field.getName()).getValue());
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new InvalidPDUException();
            }
        }
    }
    
    public String getResourceName() {
        return resource;
    }
}
