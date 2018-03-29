package apidoccer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import jsonparser.DictObject;
import jsonparser.JsonArray;
import jsonparser.JsonExposed;
import se.dscore.MasterView;


/* This code snippet generates a JSON file with the API listing of the project
 */
public class Apidoccer {

    public void list(String uptoNow, Class<?> cur) {
        
        Field[] fields = cur.getFields();
        
        for (Field field : fields) {
            
            Class<?> type = field.getType();
            JsonExposed jexp = field.getAnnotation(JsonExposed.class);
            if(jexp == null)
                continue;
            
            putAPI(jexp.comment(), uptoNow + "/" + field.getName());
            
            if((isPrimitive(type) || type == String.class)) {
                
            } else if(HashMap.class.isAssignableFrom(type)) {
                ParameterizedType ptype = (ParameterizedType) field.getGenericType();
                list(uptoNow + "/" + field.getName() + "/${key}", (Class<?>) ptype.getActualTypeArguments()[1]);
            } else if(type.isArray()) {
                list(uptoNow + "/" + field.getName() + "/${index}", type.getComponentType());
            } else {
                list(uptoNow + "/" + field.getName(), field.getType());
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
        list("/status", MasterView.class);
        putTrailer();
    }
    
    public static void main(String[] args) {
        new Apidoccer();
        System.out.println("");
    }

    private void putcomment(String comment) {
        System.out.print("<tr><td>" + comment + "</td></tr>");
    }

    private void putAPI(String comment, String url) {
        putcomment(comment);
        System.out.print("<tr><td>" + url + "</td></tr>");
    }

    private void putTrailer() {
        System.out.print("</table></body></html>");
    }

    private void putHeader() {
        System.out.print("<html><head><link rel=\"stylesheet\" href=\"styles.css\" /></head><body><table>");
    }
    
}
