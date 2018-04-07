package se.dscore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Set;
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

    public void shutDownNode(String nodename) throws Exception {
        NodeProxy np = nodes.get(nodename);
        Set<String> keys = np.getProcesses().keySet();
        for (String pid : keys) {
            try {
                np.kill(pid);
            } catch(Exception ex) {
                throw new Exception("Killing process [" + pid + "] failed." + ex.getMessage());
            }
        }
        synchronized (nodes) {
            nodes.remove(nodename);
        }
    }

    public void shutDownDomain() throws Exception {
        for (String npn : nodes.keySet()) {
            try {
                shutDownNode(npn);
            } catch (Exception ex) {
                throw new Exception("Shutting down the node [" + npn + "] failed");
            }
        }
    }
    
    @RESTExposedMethod(comment = "This can be used to stop the node. <code>Data format {'node': 'Node-ID'}</code>")
    public String shutDownNode(JsonObject data) {

        DictObject dobj = (DictObject) data;
        String nodename = (String) dobj.get("node").getValue();
        JsonArray earr = new JsonArray();
        DictObject resp = new DictObject();

        try {
            shutDownNode(nodename);
        } catch (Exception ex) {
            earr.add(new StringObject(ex.getMessage()));
        }
        
        resp.set("errors", earr);
        return resp.toString();
    }

    @RESTExposedMethod(comment = "Shutsdown the whole domain <code>Data format {}</code>")
    public String shutDownDomain(JsonObject data) {
        DictObject resp = new DictObject();
        JsonArray jarr = new JsonArray();
        try {
            shutDownDomain();
        } catch(Exception ex) {
            jarr.add(new StringObject(ex.getMessage()));
        }
        resp.set("errors", jarr);
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
