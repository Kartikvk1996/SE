package apidoccer;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsonparser.DictObject;
import jsonparser.JsonArray;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;
import jsonparser.StringObject;
import se.dscore.MasterView;
import se.dscore.RESTExposedMethod;


/* This code snippet generates a JSON file with the API plisting of the project
 */
public class Apidoccer {

    static PrintStream xout;
    JsonArray proplist = new JsonArray();
    JsonArray methList = new JsonArray();

    private static void sop(String string) {
        xout.print(string);
    }

    private void mlist(String uptoNow, Class<?> cur) {

        if (isPrimitive(cur)) {
            return;
        }

        Method meths[] = cur.getDeclaredMethods();
        for (Method meth : meths) {
            RESTExposedMethod annmeth = meth.getAnnotation(RESTExposedMethod.class);
            if (annmeth != null) {
                methList.add(putAPI(meth, uptoNow + "/" + meth.getName()));
            }
        }

        Field[] fields = cur.getFields();

        for (Field field : fields) {

            Class<?> type = field.getType();
            if (isPrimitive(type)) {
                continue;
            }

            if (HashMap.class.isAssignableFrom(type)) {
                ParameterizedType ptype = (ParameterizedType) field.getGenericType();
                mlist(uptoNow + "/" + field.getName() + "/${key}", (Class<?>) ptype.getActualTypeArguments()[1]);
            } else if (type.isArray()) {
                mlist(uptoNow + "/" + field.getName() + "/${index}", type.getComponentType());
            } else {
                mlist(uptoNow + "/" + field.getName(), field.getType());
            }
        }

    }

    public final void plist(String uptoNow, Class<?> cur) {

        Field[] fields = cur.getFields();

        for (Field field : fields) {

            Class<?> type = field.getType();
            JsonExposed jexp = field.getAnnotation(JsonExposed.class);
            if (jexp == null) {
                continue;
            }

            proplist.add(putAPI(field, uptoNow + "/" + field.getName()));

            if ((isPrimitive(type) || type == String.class)) {

            } else if (HashMap.class.isAssignableFrom(type)) {
                ParameterizedType ptype = (ParameterizedType) field.getGenericType();
                plist(uptoNow + "/" + field.getName() + "/${key}", (Class<?>) ptype.getActualTypeArguments()[1]);
            } else if (type.isArray()) {
                plist(uptoNow + "/" + field.getName() + "/${index}", type.getComponentType());
            } else {
                plist(uptoNow + "/" + field.getName(), field.getType());
            }
        }
    }

    public static boolean isPrimitive(Class<?> type) {
        return (type.isPrimitive() && type != void.class)
                || type == Double.class || type == Float.class || type == Long.class
                || type == Integer.class || type == Short.class || type == Character.class
                || type == Byte.class || type == Boolean.class || type == String.class;
    }

    public Apidoccer() {

        Class[] classes = {
            MasterView.class
        };

        for (Class c : classes) {
            plist("/status", c);
            mlist("/exec", c);
        }

        DictObject apilist = new DictObject();
        apilist.set("properties", proplist);
        apilist.set("methods", methList);

        sop("var apilist = ");
        sop(apilist.toString());

    }

    public static void main(String[] args) {
        try {
            //xout = System.out;
            xout = new PrintStream("C:\\Users\\mpataki\\Documents\\se\\java\\beats\\apilist.js");
            Apidoccer api = new Apidoccer();
            sop("\n");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Apidoccer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JsonObject putAPI(Field f, String url) {
        DictObject dObj = new DictObject();
        dObj.set("type", f.getType().toString());
        dObj.set("url", url);
        dObj.set("comment", (f.getAnnotation(JsonExposed.class)).comment());
        return dObj;
    }

    private JsonObject putAPI(Method meth, String url) {
        DictObject dObj = new DictObject();
        dObj.set("returntype", meth.getReturnType().getName());
        dObj.set("url", url);
        dObj.set("comment", (meth.getAnnotation(RESTExposedMethod.class)).comment());
        JsonArray jarr = new JsonArray();
        for (Class<?> param : meth.getParameterTypes()) {
            jarr.add(new StringObject(param.getName()));
        }
        dObj.set("inputTypes", jarr);
        return dObj;
    }

}
