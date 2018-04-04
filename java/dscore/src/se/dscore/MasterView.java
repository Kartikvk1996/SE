package se.dscore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import jsonparser.Json;
import jsonparser.JsonException;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;

public class MasterView {

    @JsonExposed(comment = "This is a list of slaves. Access the elements using the key")
    public LinkedHashMap<String, NodeProxy> slaves;

    public MasterView(LinkedHashMap<String, NodeProxy> slaves) {
        this.slaves = slaves;
    }
    
    public String getObjectAsJson(String url) {
        
        String chunks[] = url.split("/");
        
        try {
            Object obj = APIFinder.getObject(this, chunks, 1, chunks.length);
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

    

    public String execute(String url, JsonObject obj) {
        
        String chunks[] = url.split("/");
        try {
            Object endObj = APIFinder.getObject(this, chunks, 1, chunks.length - 1);
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
