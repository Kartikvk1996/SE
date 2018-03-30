package apidoccer;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsonparser.JsonExposed;
import se.dscore.MasterView;
import se.dscore.RESTExposedMethod;


/* This code snippet generates a JSON file with the API plisting of the project
 */
public class Apidoccer {

    static PrintStream xout;

    private static void sop(String string) {
        xout.print(string);
    }

    private void mlist(String uptoNow, Class<?> cur) {

        if(isPrimitive(cur))
            return;
        
        Method meths[] = cur.getDeclaredMethods();
        for (Method meth : meths) {
            RESTExposedMethod annmeth = meth.getAnnotation(RESTExposedMethod.class);
            if (annmeth != null) {
                putAPI(annmeth.comment(), uptoNow + "/" + meth.getName());
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

    public void plist(String uptoNow, Class<?> cur) {

        Field[] fields = cur.getFields();

        for (Field field : fields) {

            Class<?> type = field.getType();
            JsonExposed jexp = field.getAnnotation(JsonExposed.class);
            if (jexp == null) {
                continue;
            }

            putAPI(jexp.comment(), uptoNow + "/" + field.getName());

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
        putHeader();

        sop("<h3>Properties</h3>");
        plist("/status", MasterView.class);

        sop("<h3>Methods</h3>");
        mlist("/exec", MasterView.class);

        putTrailer();

    }

    public static void main(String[] args) {
        try {
            //xout = System.out;
            xout = new PrintStream("api.html");
            new Apidoccer();
            sop("\n");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Apidoccer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void putcomment(String comment) {
        sop("<div class='comment'>" + comment + "</div>");
    }

    private void putAPI(String comment, String url) {
        sop("<div class='border-div'>");
        putcomment(comment);
        sop("<div class='url'>" + url + "</div>");
        sop("</div>");
    }

    private void putTrailer() {
        sop("</body></html>");
    }

    private void putHeader() {
        sop("<html><head><link rel=\"stylesheet\" href=\"styles.css\" /></head><body>");
    }

}
