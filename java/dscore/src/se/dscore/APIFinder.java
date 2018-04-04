package se.dscore;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import jsonparser.JsonException;
import jsonparser.JsonExposed;

public class APIFinder {
    
    public static Object getObject(Object obj, String chunks[], int index, int count)
            throws IllegalArgumentException, IllegalAccessException,
            JsonException, NoSuchFieldException, APIAccessException {
        
        if (index > count) {
            return null;
        }

        if (index == count) {
            return obj;
        }

        if (isInteger(chunks[index])) {
            int offset = Integer.parseInt(chunks[index]);
            if (obj.getClass().isArray()) {
                return getObject(Array.get(obj, offset), chunks, index + 1, count);
            } else if (List.class.isAssignableFrom(obj.getClass())) {
                return getObject(((List)obj).get(offset), chunks, index + 1, count);
            }
        }

        Field field = obj.getClass().getField(chunks[index]);
        
        if( field.getAnnotation(JsonExposed.class) == null)
            throw new APIAccessException("Value not exposed");
        
        if (HashMap.class.isAssignableFrom(field.getType()) && (index + 1) < chunks.length) {
            return getObject(((HashMap) field.get(obj)).get(chunks[index + 1]), chunks, index + 2, count);
        }
        
        return getObject(field.get(obj), chunks, index + 1, count);
    }

    private static boolean isInteger(String chunk) {
        try {
            Integer.parseInt(chunk);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
