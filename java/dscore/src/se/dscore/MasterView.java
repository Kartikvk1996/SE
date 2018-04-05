package se.dscore;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsonparser.DictObject;
import jsonparser.Json;
import jsonparser.JsonArray;
import jsonparser.JsonException;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;
import jsonparser.StringObject;

public class MasterView {

    @JsonExposed(comment = "This is a list of slaves. Access the elements using the key")
    public final LinkedHashMap<String, NodeProxy> nodes;

    public MasterView(LinkedHashMap<String, NodeProxy> nodes) {
        this.nodes = nodes;
    }

    public String getObjectAsJson(String url) {

        String chunks[] = url.split("/");

        try {
            Object obj = APIFinder.getObject(this, chunks, 1, chunks.length);
            if (obj != null) {
                return Json.dump(obj);
            } else {
                return "{'error': 'object not found'}";
            }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | JsonException ex) {
        } catch (APIAccessException ex) {
            return "{'error': 'Field not exposed'}";
        }
        return "{}";
    }

    @RESTExposedMethod(comment = "This can be used to stop the node. <pre>Data format {'node': 'Node-ID'}</pre>")
    public String shutDownNode(JsonObject data) {

        DictObject dobj = (DictObject) data;
        String nodename = (String) dobj.get("node").getValue();
        NodeProxy np = nodes.get(nodename);
        JsonArray earr = new JsonArray();
        DictObject resp = new DictObject();
        Set<String> keys = np.getProcesses().keySet();

        for (String pid : keys) {
            try {
                np.kill(pid);
            } catch (Exception ex) {
                earr.add(new StringObject("Killing process [" + pid + "] failed." + ex.getMessage()));
            }
        }

        synchronized (nodes) {
            nodes.remove(nodename);
        }
        resp.set("errors", earr);
        return resp.toString();
    }

    public String execute(String url, JsonObject obj) {

        String chunks[] = url.split("/");
        try {
            Object endObj = APIFinder.getObject(this, chunks, 1, chunks.length - 1);
            Method meth = endObj.getClass().getMethod(chunks[chunks.length - 1], JsonObject.class);
            if (meth.getAnnotation(RESTExposedMethod.class) != null) {
                return (String) meth.invoke(endObj, obj);
            } else {
                return "{'error': 'This API is not exposed'}";
            }
        } catch (IllegalArgumentException | IllegalAccessException | JsonException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
        } catch (APIAccessException ex) {
            return "{'error': 'The field is not exposed through REST API'}";
        }
        return "{'error': 'No such method'}";
    }

}
