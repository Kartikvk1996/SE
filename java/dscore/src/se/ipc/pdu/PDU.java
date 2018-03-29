package se.ipc.pdu;

import java.lang.reflect.Field;
import jsonparser.DictObject;
import jsonparser.JsonAssignable;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;

public class PDU {

    @JsonExposed
    public String who;
    
    @JsonExposed
    public int method;
    
    @JsonExposed
    public JsonObject data;

    private static String PROCESS_ROLE;

    public PDU(DictObject jObject) throws InvalidPDUException {
        try {
            for (Field field : getClass().getFields()) {
                Object value = jObject.get(field.getName()).getValue();
                if (field.getType().isPrimitive()) {
                    field.set(this, value);
                } else {
                    try {   //You have no guarantee that there is a constructor
                        Object nobj = field.getType().newInstance();
                        if (nobj instanceof JsonAssignable) {
                            field.set(this, nobj);
                            ((JsonAssignable) nobj).initialize((JsonObject) value);
                        } else {
                            field.set(this, value);
                        }
                    } catch (InstantiationException iex) {
                        field.set(this, value);
                    }
                }
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

    public JsonObject getData() {
        return data;
    }
    
    public static void setProcessRole(String role) {
        PROCESS_ROLE = role;
    }

}
