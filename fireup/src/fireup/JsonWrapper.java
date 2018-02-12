package fireup;

import json.*;

/* reqresent a case-insensitive Json Object */
class JsonWrapper {

    JsonObject json;

    public JsonWrapper() {
        json = new JsonObject();
    }

    public JsonWrapper(String data) {
        try {
            json = Json.parse(data).asObject();
        } catch(Exception ex) {
            System.err.println(ex);
        }
    }

    public String get(String key) {
        String chunks[] = key.split("[.]");
        JsonObject obj = json;
        for (int i = 0; i < chunks.length - 1; i++) {
            obj = obj.get(chunks[i]).asObject();
        }
        return obj.get(chunks[chunks.length - 1]).asString();
    }

    public final void set(String key, Object value) {
        json.add(key, value.toString());
    }

    void setFixedParams( String senderip, String senderport,
            String rcvrip, String rcvrport) {
        set("WHO", "FIREUP");
        set("WHOM", "MASTER");
        set("SENDERIP", senderip);
        set("SENDERPORT", senderport);
        set("RECIEVERIP", rcvrip);
        set("RECEIVERPORT", rcvrport);
    }
    
    @Override
    public String toString() {
        return json.toString();
    }
}
