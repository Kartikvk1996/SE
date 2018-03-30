package se.dscore;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import jsonparser.Json;
import jsonparser.JsonException;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;

public class MasterView {

    @JsonExposed(comment = "This is a list of slaves. Access the elements using the key")
    public LinkedHashMap<String, SlaveProxy> slaves;

    public MasterView(LinkedHashMap<String, SlaveProxy> slaves) {
        this.slaves = slaves;
    }
    
    public String getObjectAsJson(String url) {
        
        String chunks[] = url.split("/");
        
        try {
            Object obj = getObject(this, chunks, 1, chunks.length);
            if(obj != null)
                return Json.dump(obj);
            else
                return "{'error': 'object not found'}";
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | JsonException ex) {
        } catch (APIAccessException ex) {
            return "{'error': 'Field not exposed'}";
        }
        return "{}";
    }

    public Object getObject(Object obj, String chunks[], int index, int count)
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

    private boolean isInteger(String chunk) {
        try {
            Integer.parseInt(chunk);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public String execute(String url, JsonObject obj) {
        
        String chunks[] = url.split("/");
        try {
            Object endObj = getObject(this, chunks, 1, chunks.length - 1);
            Method meth = endObj.getClass().getMethod(chunks[chunks.length - 1], JsonObject.class);
            if(meth.getAnnotation(RESTExposedMethod.class) != null)
                return (String) meth.invoke(endObj, obj);
            else
                return "{'error': 'This API is not exposed'}";
        } catch (IllegalArgumentException | IllegalAccessException | JsonException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
        } catch (APIAccessException ex) { 
            return "{'error': 'The field is not exposed through REST API'}";
        }
        return "{'error': 'No such method'}";
    }

}
