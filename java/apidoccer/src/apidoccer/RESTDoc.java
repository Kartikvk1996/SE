package apidoccer;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import jsonparser.DictObject;
import jsonparser.JsonArray;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;
import jsonparser.StringObject;
import se.dscore.MasterView;
import se.dscore.RESTExposedMethod;

public class RESTDoc {

    public RESTDoc(PrintStream out) {
        
        xout = out;
        
    }

    static PrintStream xout;

    private static void sop(String string) {
        xout.print(string);
    }

}
