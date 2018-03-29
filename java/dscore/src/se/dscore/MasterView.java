package se.dscore;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import jsonparser.Json;
import jsonparser.JsonException;

public class Status {

    public LinkedHashMap<String, SlaveProxy> slaves;

    public Status(LinkedHashMap<String, SlaveProxy> slaves) {
        this.slaves = slaves;
    }
    
    public String getObject(String url) {

        String chunks[] = url.split("/");
        
        try {
            return getObject(this, chunks, 1);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | JsonException ex) {
        }
        return "{}";
    }

    public String getObject(Object obj, String chunks[], int index)
            throws IllegalArgumentException, IllegalAccessException,
            JsonException, NoSuchFieldException {

        if (index > chunks.length) {
            return "{}";
        }

        if (index == chunks.length) {
            return Json.dump(obj);
        }

        if (isInteger(chunks[index])) {
            int offset = Integer.parseInt(chunks[index]);
            if (obj.getClass().isArray()) {
                return getObject(Array.get(obj, offset), chunks, index + 1);
            } else if (List.class.isAssignableFrom(obj.getClass())) {
                return getObject(((List)obj).get(offset), chunks, index + 1);
            }
        }

        Field field = obj.getClass().getField(chunks[index]);

        if (HashMap.class.isAssignableFrom(field.getType()) && (index + 1) < chunks.length) {
            return getObject(((HashMap) field.get(obj)).get(chunks[index + 1]), chunks, index + 2);
        }
        
        return getObject(field.get(obj), chunks, index + 1);
    }

    private boolean isInteger(String chunk) {
        try {
            Integer.parseInt(chunk);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

}
