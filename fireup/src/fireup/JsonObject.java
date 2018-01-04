package fireup;

import java.util.HashMap;

/* reqresent a case-insensitive Json Object */
class JsonObject {

    private HashMap<String, String> dict;

    public JsonObject() {
        dict = new HashMap<>();
    }

    public JsonObject(String data) {

        dict = new HashMap<>();

        /* Really dirty code due to laziness for parsing json.
         * Assuming the simple (not nested) json */
        String chunks[] = data.split("[:,]");
        try {
            for (int i = 0; i < chunks.length; i += 2) {
                chunks[i] = chunks[i].split("[\"']")[1];
                chunks[i + 1] = chunks[i + 1].split("[\"']")[1];
                set(chunks[i], chunks[i + 1]);
            }
        } catch (Exception ex) {
            System.out.println("Invalid JSON recieved");
        }
    }

    public String get(String key) {
        return dict.get("\"" + key.toUpperCase() + "\"").split("\"")[1];
    }

    public final void set(String key, Object value) {
        dict.put("\"" + key.toUpperCase() + "\"", "\"" + value.toString() + "\"");
    }

    @Override
    public String toString() {
        return dict.toString().replaceAll("=", ":") + "\n";
    }
}
