package se.ipc.pdu;

import java.lang.reflect.Field;
import jsonparser.DictObject;
import jsonparser.JsonObject;

public class PDU {

    public String who;
    public int method;
    public JsonObject data;

    private static String PROCESS_ROLE;
    
    public PDU(DictObject jObject) throws InvalidPDUException {
        try {
            for (Field field : getClass().getFields()) {
                field.set(this, jObject.get(field.getName()).getValue());
            }
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
            throw new InvalidPDUException();
        }
    }

    public PDU(int method) {
        this.method = method;
        this.who = PROCESS_ROLE;
        this.data = new DictObject();
    }
    
    public String getWho() {
        return who;
    }
    
    public int getMethod() {
        return method;
    }
    
    public String getSecret() {
        return "";
    }
    
    public void setData(JsonObject data) {
        this.data = data;
    }
    
    public static void setProcessRole(String role) {
        PROCESS_ROLE = role;
    }
    
}
