package se.ipc.pdu;

import java.lang.reflect.Field;
import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class ErrorPDU extends PDU {

    @JsonExposed public String errMessage;

    public ErrorPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.set(this, jObject.get(field.getName()).getValue());
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new InvalidPDUException();
            }
        }
    }
    
    public ErrorPDU(String msg) {
        super(PDUConsts.METHOD_ERROR);
        this.errMessage = msg;
    }
    
}
