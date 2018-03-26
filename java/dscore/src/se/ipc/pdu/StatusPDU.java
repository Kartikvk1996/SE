package se.ipc.pdu;

import java.lang.reflect.Field;
import jsonparser.DictObject;

public class StatusPDU extends PDU {

    public StatusPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.set(this, jObject.get(field.getName()).getValue());
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new InvalidPDUException();
            }
        }
    }

    public StatusPDU() {
        super(PDUConsts.METHOD_STATUS);
    }
}
