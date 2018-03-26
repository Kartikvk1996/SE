package se.ipc.pdu;

import java.lang.reflect.Field;
import jsonparser.DictObject;

public class CreatePDU extends PDU {

    public String executable;
    public String arguments;

    public CreatePDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
        try {
            for (Field field : getClass().getDeclaredFields()) {
                field.set(this, jObject.get(field.getName()).getValue());
            }
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
            throw new InvalidPDUException();
        }
    }

    public CreatePDU(String executable, String arguments) {
        super(PDUConsts.METHOD_CREATE);
        this.executable = executable;
        this.arguments = arguments;
    }
    
    public String getExecutable() {
        return executable;
    }

    public String[] getArguments() {
        return arguments.split("[ \t\n]");
    }

}
