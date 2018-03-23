package se.ipc.pdu;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import json.Json;
import json.JsonObject;

public class PDU {

    private static String PROCESS_ROLE;
    public final static String DATA = "DATA";
    public final static String METHOD_INTRO = "INTRO";
    public final static String METHOD_STATUS = "STATUS";
    public final static String METHOD_CREATE = "CREATE";
    public final static String PN_GUEST = "GUEST";
    public final static String PN_FIREUP = "FIREUP";
    public final static String METHOD_CONNECT = "CONNECT";
    public final static String METHOD_UPDATE = "UPDATE";
    public final static String METHOD_GET = "GET";
    public final static String METHOD_ACK = "ACK";
    private static String WHO = "WHO";
    private static String METHOD = "METHOD";
    private JsonObject jObj;

    public static PDU fromStream(InputStream iStream) throws IOException {
        
        PDU pdu = new PDU();
        pdu.jObj = Json.parse(new InputStreamReader(iStream)).asObject();
        return pdu;
    }
    
    protected JsonObject jData = new JsonObject();

    public static void setProcessRole(String role) {
        PROCESS_ROLE = role;
    }
    
    final void setWho(String pname) {
        jObj.set(WHO, pname.toUpperCase());
    }

    public PDU(String method) {
        jObj = new JsonObject();
        setMethod(method);
        setWho(PROCESS_ROLE);
    }

    public PDU() {
        jObj = new JsonObject();
        setWho(PROCESS_ROLE);
    }

    public void setJData(JsonObject jobj) {
        jData = jobj;
    }

    public String getMethod() {
        return jObj.get(METHOD).asString();
    }

    public void setMethod(String method) {
        jObj.set(METHOD, method);
    }

    /* separate function to load data/PDU*/
    public void setData(String data) {
        jData = Json.parse(data).asObject();
    }

    public String getWho() {
        return jObj.get(WHO).asString();
    }

    public JsonObject getJSON() {
        return jObj;
    }

    private PDU(String jsonString, boolean waste) {
        jObj = Json.parse(jsonString).asObject();
    }
    
    public static PDU fromString(String jsonString) {
        return new PDU(jsonString, true);
    }

    @Override
    public String toString() {
        return getJSON().toString();
    }

    public String getData() {
        return jObj.get(DATA).asString();
    }

    public JsonObject getDataAsJson() {
        return jData;
    }

    public String getValue(String dsv) {
        String chunks[] = dsv.split(".");
        JsonObject json = jObj;
        for (String chunk : chunks) {
            json = json.get(chunk).asObject();
            if(json == null)
                return "";
        }
        return json.asString();
    }

    public void setValue(String dsv, String value) throws JsonPathNotExistsException {
        String chunks[] = dsv.split(".");
        JsonObject parent = jObj;
        for (int i = 0; i < chunks.length-1; i++) {
            parent = parent.get(chunks[i]).asObject();
            if(parent == null) {
                throw new JsonPathNotExistsException("path doesn't exist");
            }
        }
        parent.set(chunks[chunks.length - 1], value);
    }
}
